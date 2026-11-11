# pets

A [picocli](https://picocli.info/) command line client for the Swagger Petstore in [`pet.yaml`](pet.yaml). Responses are printed with `DisplayBuilder`.

The default server is `http://petstore.swagger.io/v1`. A flag on the command line wins over the environment variable.

Requires Java 11 or newer. Install the prettytable jar into the local Maven repository first (the example depends on `io.github.rickwporter:prettytable:1.0.0`).

```bash
mvn -q package
java -jar target/pets.jar list --limit 5
```

`make test` runs the example tests. The tests talk to an in-process stub, not the public server.

## Commands

The commands follow `pet.yaml`: `list` is `GET /pets`, `add` is `POST /pets`, `show` is `GET /pets/{petId}`, and `delete` is `DELETE /pets/{petId}`.

Global options can go before the command or after it:

| Option | Environment variable | Meaning |
| --- | --- | --- |
| `-s`, `--server` | `API_HOST` | Petstore base URL. `http://petstore.swagger.io/v1` is used when both are omitted. |
| `-k`, `--api-key` | `API_KEY` | Bearer token. Omitted when both are empty. |
| `-f`, `--format` | `OUTPUT_FORMAT` | `text`, `rich`, `csv`, `html`, or `json`. `rich` when both are omitted. |
| `-h`, `--help` | | Help for the current command |

```bash
# First page holds at most 5 pets. Further pages come from the x-next header.
java -jar target/pets.jar list --limit 5

# One pet, as a Property / Value table.
java -jar target/pets.jar show 1

# Create a pet. id, name, and owner are required. tag is optional.
# The spec responds with an empty 201 body.
java -jar target/pets.jar add --id 7 --name Fido --owner Rick --tag trained

java -jar target/pets.jar delete 1
```

`list --limit` is sent to the server and must be from 1 to 100. When the response includes an `x-next` header, `list` follows that link and prints the combined pages. `show` and `add` print the pet fields `id`, `name`, `tag`, and `owner`. `add` and `delete` print `Empty response.` when the server returns no body.

`mvn exec:java -Dexec.args="list --limit 5"` runs the same main class without the shaded jar.
