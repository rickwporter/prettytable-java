package rickwporter.prettytable.example.pets;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;

public class PetsCliTest {
    private PetstoreStub stub;

    @BeforeEach
    public void setUp() throws Exception {
        this.stub = PetstoreStub.start();
    }

    @AfterEach
    public void tearDown() {
        if (this.stub != null) {
            this.stub.close();
        }
    }

    @Test
    public void testHelp() {
        Result result = this.runRaw("--help");
        Assertions.assertEquals(0, result.exit, result.dump());
        Assertions.assertTrue(result.out.contains("list"), result.dump());
        Assertions.assertTrue(result.out.contains("show"), result.dump());
        Assertions.assertTrue(result.out.contains("add"), result.dump());
        Assertions.assertTrue(result.out.contains("delete"), result.dump());
        Assertions.assertFalse(result.out.contains("inventory"), result.dump());
    }

    @Test
    public void testMissingCommand() {
        Result result = this.runRaw();
        Assertions.assertEquals(CommandLine.ExitCode.USAGE, result.exit, result.dump());
        Assertions.assertTrue(result.err.contains("Usage:"), result.dump());
    }

    @Test
    public void testListSendsLimitAndFollowsNext() {
        Result all = this.run("--format", "text", "list");
        Assertions.assertEquals(0, all.exit, all.dump());
        Assertions.assertTrue(all.out.contains("Fido"), all.dump());
        Assertions.assertTrue(all.out.contains("Whiskers"), all.dump());
        Assertions.assertTrue(all.out.contains("Found 2 items"), all.dump());
        Assertions.assertNull(this.stub.lastQuery());
        Assertions.assertEquals("/pets", this.stub.lastPath());
        Assertions.assertEquals(1, this.stub.queries().size());

        Result limited = this.run("--format", "text", "list", "--limit", "1");
        Assertions.assertEquals(0, limited.exit, limited.dump());
        Assertions.assertTrue(limited.out.contains("Fido"), limited.dump());
        Assertions.assertTrue(limited.out.contains("Whiskers"), limited.dump());
        Assertions.assertEquals(List.of("limit=1", "page=2"), this.stub.queries().subList(1, 3));
    }

    @Test
    public void testFormats() {
        Result listed = this.run("list", "--format", "json");
        Assertions.assertEquals(0, listed.exit, listed.dump());
        Assertions.assertTrue(listed.out.contains("\"Fido\""), listed.dump());
        Assertions.assertEquals("GET", this.stub.lastMethod());
        Assertions.assertEquals("/pets", this.stub.lastPath());

        Result rich = this.run("--format", "rich", "list");
        Assertions.assertEquals(0, rich.exit, rich.dump());
        Assertions.assertTrue(rich.out.contains("┏"), rich.dump());

        Result csv = this.run("--format", "CSV", "list");
        Assertions.assertEquals(0, csv.exit, csv.dump());
        Assertions.assertTrue(csv.out.contains("Fido"), csv.dump());
    }

    @Test
    public void testShowAndApiKey() {
        Result pet = this.run("--format", "text", "--api-key", "special-key", "show", "1");
        Assertions.assertEquals(0, pet.exit, pet.dump());
        Assertions.assertEquals("special-key", this.stub.lastApiKey());
        Assertions.assertTrue(pet.out.contains("Fido"), pet.dump());
        Assertions.assertTrue(pet.out.contains("trained"), pet.dump());
        Assertions.assertTrue(pet.out.contains("Rick"), pet.dump());
        Assertions.assertTrue(pet.out.contains("Property"), pet.dump());
        Assertions.assertEquals("/pets/1", this.stub.lastPath());
    }

    @Test
    public void testAddDeleteAndErrors() throws Exception {
        Result added = this.run(
                "add",
                "--name", "Fido",
                "--id", "7",
                "--owner", "Rick",
                "--tag", "trained");
        Assertions.assertEquals(0, added.exit, added.dump());
        Assertions.assertTrue(added.out.contains("Empty response."), added.dump());
        Map<String, Object> posted = new ObjectMapper().readValue(
                this.stub.lastBody(), new TypeReference<Map<String, Object>>() { });
        Assertions.assertEquals("Fido", posted.get("name"));
        Assertions.assertEquals("Rick", posted.get("owner"));
        Assertions.assertEquals("trained", posted.get("tag"));
        Assertions.assertEquals(7, ((Number) posted.get("id")).intValue());
        Assertions.assertFalse(posted.containsKey("status"), posted.toString());
        Assertions.assertEquals("/pets", this.stub.lastPath());

        Result deleted = this.run("delete", "1");
        Assertions.assertEquals(0, deleted.exit, deleted.dump());
        Assertions.assertTrue(deleted.out.contains("Empty response."), deleted.dump());
        Assertions.assertEquals("DELETE", this.stub.lastMethod());
        Assertions.assertEquals("/pets/1", this.stub.lastPath());

        Result missing = this.run("show", "404");
        Assertions.assertEquals(CommandLine.ExitCode.SOFTWARE, missing.exit, missing.dump());
        Assertions.assertTrue(missing.err.contains("HTTP 404"), missing.dump());
        Assertions.assertTrue(missing.err.contains("Pet not found"), missing.dump());

        Result text = this.run("show", "5");
        Assertions.assertEquals(CommandLine.ExitCode.SOFTWARE, text.exit, text.dump());
        Assertions.assertTrue(text.err.contains("HTTP 400"), text.dump());
        Assertions.assertTrue(text.err.contains("nope"), text.dump());
    }

    @Test
    public void testUsageErrors() {
        Result limit = this.run("list", "--limit", "0");
        Assertions.assertEquals(CommandLine.ExitCode.USAGE, limit.exit, limit.dump());
        Assertions.assertNull(this.stub.lastPath());

        Result tooMany = this.run("list", "--limit", "101");
        Assertions.assertEquals(CommandLine.ExitCode.USAGE, tooMany.exit, tooMany.dump());
        Assertions.assertTrue(tooMany.err.contains("100"), tooMany.dump());

        Result format = this.run("--format", "nope", "list");
        Assertions.assertEquals(CommandLine.ExitCode.USAGE, format.exit, format.dump());

        Result name = this.run("add");
        Assertions.assertEquals(CommandLine.ExitCode.USAGE, name.exit, name.dump());

        Result server = this.runRaw("--server", "ftp://example.com", "list");
        Assertions.assertEquals(CommandLine.ExitCode.USAGE, server.exit, server.dump());
        Assertions.assertTrue(server.err.contains("http or https"), server.dump());

        Result unknown = this.runRaw("nope");
        Assertions.assertEquals(CommandLine.ExitCode.USAGE, unknown.exit, unknown.dump());
    }

    @Test
    public void testEnvironmentVariables() throws Exception {
        Map<String, String> env = new HashMap<String, String>();
        env.put("API_HOST", this.stub.url());
        env.put("API_KEY", "special-key");
        env.put("OUTPUT_FORMAT", "json");
        Result listed = this.runProcess(env, "list");
        Assertions.assertEquals(0, listed.exit, listed.dump());
        Assertions.assertTrue(listed.out.contains("\"Fido\""), listed.dump());
        Assertions.assertTrue(listed.out.contains("\"Whiskers\""), listed.dump());
        Assertions.assertEquals("special-key", this.stub.lastApiKey());
        Assertions.assertEquals("/pets", this.stub.lastPath());

        env.remove("API_KEY");
        env.put("OUTPUT_FORMAT", "text");
        Result pet = this.runProcess(env, "show", "1");
        Assertions.assertEquals(0, pet.exit, pet.dump());
        Assertions.assertTrue(pet.out.contains("Fido"), pet.dump());
        Assertions.assertNull(this.stub.lastApiKey());

        env.put("OUTPUT_FORMAT", "nope");
        Result bad = this.runProcess(env, "list");
        Assertions.assertEquals(CommandLine.ExitCode.USAGE, bad.exit, bad.dump());
    }

    @Test
    public void testFlagsOverrideEnvironment() throws Exception {
        Map<String, String> env = new HashMap<String, String>();
        env.put("API_HOST", "http://127.0.0.1:1");
        env.put("API_KEY", "from-env");
        env.put("OUTPUT_FORMAT", "json");
        Result result = this.runProcess(
                env,
                "--server", this.stub.url(),
                "--api-key", "special-key",
                "--format", "text", "list");
        Assertions.assertEquals(0, result.exit, result.dump());
        Assertions.assertTrue(result.out.contains("Found 2 items"), result.dump());
        Assertions.assertTrue(result.out.contains("Fido"), result.dump());
        Assertions.assertFalse(result.out.contains("\"Fido\""), result.dump());
        Assertions.assertEquals("special-key", this.stub.lastApiKey());
    }

    private Result run(String... args) {
        List<String> full = new ArrayList<String>();
        full.add("--server");
        full.add(this.stub.url());
        Collections.addAll(full, args);
        return this.runRaw(full.toArray(new String[0]));
    }

    private Result runProcess(Map<String, String> env, String... args) throws Exception {
        Path dir = Path.of("target");
        Files.createDirectories(dir);
        Path outFile = Files.createTempFile(dir, "pets-out-", ".txt");
        Path errFile = Files.createTempFile(dir, "pets-err-", ".txt");
        List<String> command = new ArrayList<String>();
        command.add(ProcessHandle.current().info().command().orElse("java"));
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        command.add(PetsCli.class.getName());
        Collections.addAll(command, args);
        ProcessBuilder builder = new ProcessBuilder(command);
        Map<String, String> processEnv = builder.environment();
        processEnv.remove("API_HOST");
        processEnv.remove("API_KEY");
        processEnv.remove("OUTPUT_FORMAT");
        processEnv.putAll(env);
        builder.redirectOutput(outFile.toFile());
        builder.redirectError(errFile.toFile());
        Process process = builder.start();
        try {
            boolean finished = process.waitFor(30, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
            }
            String stdout = Files.readString(outFile);
            String stderr = Files.readString(errFile);
            if (!finished) {
                return new Result(124, stdout, stderr + "\n(timed out)");
            }
            return new Result(process.exitValue(), stdout, stderr);
        } finally {
            Files.deleteIfExists(outFile);
            Files.deleteIfExists(errFile);
        }
    }

    private Result runRaw(String... args) {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        ByteArrayOutputStream errBytes = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(new OutputStreamWriter(outBytes, StandardCharsets.UTF_8), true);
        PrintWriter err = new PrintWriter(new OutputStreamWriter(errBytes, StandardCharsets.UTF_8), true);
        CommandLine cmd = PetsCli.commandLine();
        cmd.setOut(out);
        cmd.setErr(err);
        int exit = cmd.execute(args);
        out.flush();
        err.flush();
        return new Result(
                exit,
                outBytes.toString(StandardCharsets.UTF_8),
                errBytes.toString(StandardCharsets.UTF_8));
    }

    private static final class Result {
        private final int exit;
        private final String out;
        private final String err;

        private Result(int exitCode, String stdout, String stderr) {
            this.exit = exitCode;
            this.out = stdout;
            this.err = stderr;
        }

        private String dump() {
            return "exit " + this.exit + "\nstdout:\n" + this.out + "\nstderr:\n" + this.err;
        }
    }
}
