package rickwporter.prettytable.example.pets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.ScopeType;
import picocli.CommandLine.Spec;
import rickwporter.prettytable.DisplayBuilder;
import rickwporter.prettytable.OutputFormat;

@Command(
    name = "pets",
    mixinStandardHelpOptions = true,
    version = "pets 1.0",
    description = "Manage pets and print the response with prettytable.",
    subcommands = {
        PetsCli.ListPets.class,
        PetsCli.AddPet.class,
        PetsCli.ShowPet.class,
        PetsCli.DeletePet.class
    },
    footer = {
        "Examples:",
        "  pets list --limit 5",
        "  pets show 1",
        "  pets add --id 7 --name Fido --owner Rick --tag trained",
        "  pets delete 1"
    }
)
public final class PetsCli implements Callable<Integer> {
    static final String DEFAULT_SERVER = "http://petstore.swagger.io/v1";
    private static final String SERVER_DEFAULT = "${env:API_HOST:-" + DEFAULT_SERVER + "}";
    private static final String API_KEY_DEFAULT = "${env:API_KEY:-}";
    private static final String FORMAT_DEFAULT = "${env:OUTPUT_FORMAT:-RICH}";
    private static final int HELP_WIDTH = 120;
    private static final int MAX_LIMIT = 100;

    @Spec
    private CommandSpec spec;

    @Option(
        names = {"-s", "--server"},
        defaultValue = SERVER_DEFAULT,
        scope = ScopeType.INHERIT,
        description = "Petstore base URL. Uses API_HOST when omitted (default: ${DEFAULT-VALUE})"
    )
    private String server;

    @Option(
        names = {"-k", "--api-key"},
        defaultValue = API_KEY_DEFAULT,
        scope = ScopeType.INHERIT,
        description = "Bearer token from API_KEY. Omitted when both are empty."
    )
    private String apiKey;

    @Option(
        names = {"-f", "--format"},
        defaultValue = FORMAT_DEFAULT,
        scope = ScopeType.INHERIT,
        description = "Format: ${COMPLETION-CANDIDATES}. OUTPUT_FORMAT is the default (default: ${DEFAULT-VALUE})"
    )
    private OutputFormat format = OutputFormat.RICH;

    public static void main(String[] args) {
        int exit = commandLine().execute(args);
        System.exit(exit);
    }

    public static CommandLine commandLine() {
        CommandLine cmd = new CommandLine(new PetsCli());
        cmd.setCaseInsensitiveEnumValuesAllowed(true);
        cmd.setUsageHelpWidth(HELP_WIDTH);
        for (CommandLine sub : cmd.getSubcommands().values()) {
            sub.setCaseInsensitiveEnumValuesAllowed(true);
            sub.setUsageHelpWidth(HELP_WIDTH);
        }
        return cmd;
    }

    @Override
    public Integer call() {
        this.spec.commandLine().usage(this.spec.commandLine().getErr());
        return CommandLine.ExitCode.USAGE;
    }

    private PetstoreClient client() {
        return new PetstoreClient(this.server, this.apiKey);
    }

    private abstract static class PetCommand implements Callable<Integer> {
        @ParentCommand
        private PetsCli parent;

        @Spec
        private CommandSpec spec;

        protected PetstoreClient client() {
            return this.parent.client();
        }

        protected int execute(Callable<Integer> action) {
            try {
                return action.call();
            } catch (PetstoreException ex) {
                return this.fail(ex);
            } catch (IllegalArgumentException ex) {
                return this.usage(ex.getMessage());
            } catch (IOException ex) {
                this.spec.commandLine().getErr().println("Request failed: " + ex.getMessage());
                return CommandLine.ExitCode.SOFTWARE;
            } catch (Exception ex) {
                String message = ex.getMessage() == null ? ex.toString() : ex.getMessage();
                this.spec.commandLine().getErr().println(message);
                return CommandLine.ExitCode.SOFTWARE;
            }
        }

        protected int usage(String message) {
            this.spec.commandLine().getErr().println(message);
            return CommandLine.ExitCode.USAGE;
        }

        private String render(Object body) {
            DisplayBuilder pretty = new DisplayBuilder();
            return pretty.render(this.parent.format, body);
        }

        protected int show(Object body) {
            String rendered = this.render(body);
            PrintWriter out = this.spec.commandLine().getOut();
            if (rendered.isEmpty()) {
                out.println("Empty response.");
                return CommandLine.ExitCode.OK;
            }
            write(out, rendered);
            return CommandLine.ExitCode.OK;
        }

        private int fail(PetstoreException ex) {
            PrintWriter err = this.spec.commandLine().getErr();
            err.println(ex.getMessage());
            String rendered = this.render(ex.getBody());
            if (!rendered.isEmpty()) {
                write(err, rendered);
            }
            return CommandLine.ExitCode.SOFTWARE;
        }
    }

    @Command(
        name = "list",
        mixinStandardHelpOptions = true,
        version = "pets 1.0",
        description = "List all pets. The server pages with limit and the x-next header."
    )
    public static final class ListPets extends PetCommand {
        @Option(
            names = "--limit",
            description = "How many items to return at one time (max 100)"
        )
        private Integer limit;

        @Override
        public Integer call() {
            if (this.limit != null && (this.limit < 1 || this.limit > MAX_LIMIT)) {
                return this.usage("Limit must be from 1 to 100.");
            }
            return this.execute(() -> this.show(this.pages()));
        }

        private Object pages() throws IOException, PetstoreException {
            PetstoreClient http = this.client();
            List<Object> pets = new ArrayList<Object>();
            Set<String> seen = new HashSet<String>();
            String path = this.firstPath();
            while (path != null && seen.add(path)) {
                Object body = http.get(path);
                if (!(body instanceof List)) {
                    return pets.isEmpty() ? body : pets;
                }
                List<?> page = (List<?>) body;
                pets.addAll(page);
                String next = http.nextLink();
                boolean shortPage = this.limit != null && page.size() < this.limit;
                if (next == null || page.isEmpty() || shortPage) {
                    break;
                }
                path = next;
            }
            return pets;
        }

        private String firstPath() {
            if (this.limit == null) {
                return "/pets";
            }
            return "/pets?limit=" + this.limit;
        }
    }

    @Command(
        name = "add",
        mixinStandardHelpOptions = true,
        version = "pets 1.0",
        description = "Create a pet."
    )
    public static final class AddPet extends PetCommand {
        @Option(names = "--id", required = true, description = "Pet id")
        private long id;

        @Option(names = "--name", required = true, description = "Pet name")
        private String name;

        @Option(names = "--owner", required = true, description = "Pet owner")
        private String owner;

        @Option(names = "--tag", description = "Pet tag")
        private String tag;

        @Override
        public Integer call() {
            if (this.name == null || this.name.isBlank()) {
                return this.usage("Name is required.");
            }
            if (this.owner == null || this.owner.isBlank()) {
                return this.usage("Owner is required.");
            }
            Map<String, Object> pet = petBody(this.id, this.name.trim(), this.owner.trim(), this.tag);
            return this.execute(() -> this.show(this.client().post("/pets", pet)));
        }

        private static Map<String, Object> petBody(long id, String name, String owner, String tag) {
            Map<String, Object> pet = new LinkedHashMap<String, Object>();
            pet.put("id", id);
            pet.put("name", name);
            if (tag != null && !tag.isBlank()) {
                pet.put("tag", tag.trim());
            }
            pet.put("owner", owner);
            return pet;
        }
    }

    @Command(
        name = "show",
        mixinStandardHelpOptions = true,
        version = "pets 1.0",
        description = "Info for a specific pet."
    )
    public static final class ShowPet extends PetCommand {
        @Parameters(index = "0", paramLabel = "PET_ID", description = "The id of the pet to retrieve")
        private String petId;

        @Override
        public Integer call() {
            return this.execute(() -> this.show(this.client().get("/pets/" + pathId(this.petId))));
        }
    }

    @Command(
        name = "delete",
        mixinStandardHelpOptions = true,
        version = "pets 1.0",
        description = "Delete a pet."
    )
    public static final class DeletePet extends PetCommand {
        @Parameters(index = "0", paramLabel = "PET_ID", description = "The id of the pet to retrieve")
        private String petId;

        @Override
        public Integer call() {
            return this.execute(() -> this.show(this.client().delete("/pets/" + pathId(this.petId))));
        }
    }

    private static String pathId(String petId) {
        return URLEncoder.encode(petId, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private static void write(PrintWriter writer, String text) {
        writer.print(text);
        if (!text.endsWith("\n")) {
            writer.println();
        } else {
            writer.flush();
        }
    }
}
