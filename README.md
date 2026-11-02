# prettytable-java

![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)

Display data in a tabular form with Java

The data can be output in several different different forms:
* **text** - attractive ASCII format
* **csv** - comma separated values format
* **html** - HyperText Markup Language (HTML) format
* **json** - JavaScript Object Notation (JSON) format

Here's an example of the standard **text** output:
```
+------------+----------+------------+
|  Column A  | Column 2 | Column III |
+------------+----------+------------+
|     A      |    B     |     c      |
|     1      |   1024   |   65535    |
| Washington |  Adams   | Jefferson  |
+------------+----------+------------+
```

The `PrettyTable` class handles a table as shown above. The `TableBuilder` class can be used to turn a `Map` or `List` object into a `PrettyTable`.

## PrettyTable Usage
Here are the basics for how to create a `PrettyTable` as shown above:
```Java
import rickwporter.prettytable.PrettyTable;
import rickwporter.prettytable.PrettyTable.OutputFormat;

      PrettyTable table = new PrettyTable("Column A", "Column 2", "Column III");
      table.addRow("A", "B", "c");
      table.addRow(1, 1024, 65535);
      table.addRow("Washington", "Adams", "Jefferson");

      System.out.println(table.formattedString(OutputFormat.TEXT));
```

You can use `PrettyTable::toString()` as an alternative to `PrettyTable::formattedString()`. If you want a format other than **text**, you can set it with `PrettyTable::setOutputFormat()`. Repeated leading cells in **text** and **html** follow `PrettyTable::setRemoveRedundant()`, which removes them by default. The code below would replace the `println()` call from above:
```Java
      table.setOutputFormat(OutputFormat.HTML);
      System.out.println(table.toString());
```

The above pattern is useful, particularly if nesting tables.

### Additional Output Formats
Using the **csv** format (e.g. `OutputFormat.CSV`), you get the following output:
```
Column A,Column 2,Column III
A,B,c
1,1024,65535
Washington,Adams,Jefferson
```

Using the **html** format (e.g. `OutputFormat.HTML`), you get the following output:
```
<table>
    <thead>
        <tr>
            <th>Column A</th>
            <th>Column 2</th>
            <th>Column III</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>A</td>
            <td>B</td>
            <td>c</td>
        </tr>
        <tr>
            <td>1</td>
            <td>1024</td>
            <td>65535</td>
        </tr>
        <tr>
            <td>Washington</td>
            <td>Adams</td>
            <td>Jefferson</td>
        </tr>
    </tbody>
</table>
```

Using the **json** format (e.g. `OutputFormat.JSON`), you get the following output:
```
[
    [
        "Column A",
        "Column 2",
        "Column III"
    ],
    {
        "Column A": "A",
        "Column 2": "B",
        "Column III": "c"
    },
    {
        "Column A": 1,
        "Column 2": 1024,
        "Column III": 65535
    },
    {
        "Column A": "Washington",
        "Column 2": "Adams",
        "Column III": "Jefferson"
    }
]
```

### Alignment
Both ASCII **text** and **html** formats support different alignments. Here's how you would update the above table:
```Java
import rickwporter.prettytable.PrettyTable.HorizontalAlign;

      table.setHorizAligns(HorizontalAlign.LEFT, HorizontalAlign.CENTER, HorizontalAlign.RIGHT);
```

Now, when the **text** output is generated, the output will look like this:
```
+------------+----------+------------+
| Column A   | Column 2 | Column III |
+------------+----------+------------+
| A          |    B     |          c |
| 1          |   1024   |      65535 |
| Washington |  Adams   |  Jefferson |
+------------+----------+------------+
```

The **html** output will have the appropriate `style="text-align:xxx"` attribute on each `<th>` and `<td>` tag. So, second row in the table above would look like:
```
    <tr>
        <td style="text-align:left">1</td>
        <td style="text-align:center">1024</td>
        <td style="text-align:right">65535</td>
    </tr>
```

### Text options
ASCII **text** output can hide the header row, drop the border, draw a rule under data rows, and print a caption under the table. **csv**, **html**, and **json** ignore these settings.

`PrettyTable::setShowHeader(false)` omits the header. Column widths then come only from the data cells, so the columns can get narrower. A table built with no headers already skips that row.

```Java
      table.setShowHeader(false);
```

The **text** output then looks like this:
```
+------------+-------+-----------+
|     A      |   B   |     c     |
|     1      | 1024  |   65535   |
| Washington | Adams | Jefferson |
+------------+-------+-----------+
```

`PrettyTable::setShowLines(false)` omits the `+---+` rules and the `|` borders. Each cell keeps its padding, and column alignment still applies.

```Java
      table.setShowLines(false);
```

```
  Column A   Column 2  Column III 
     A          B          c      
     1         1024      65535    
 Washington   Adams    Jefferson  
```

`PrettyTable::setRowDividers(true)` draws a `+---+` rule under data rows. `setShowLines(false)` hides those rules with the rest of the border.

The sample rows do not repeat a leading cell, so each data row has a rule under it. The rule under the last row closes the table.

```Java
      table.setRowDividers(true);
```

```
+------------+----------+------------+
|  Column A  | Column 2 | Column III |
+------------+----------+------------+
|     A      |    B     |     c      |
+------------+----------+------------+
|     1      |   1024   |   65535    |
+------------+----------+------------+
| Washington |  Adams   | Jefferson  |
+------------+----------+------------+
```

When repeated leading cells are removed, a row that shares a leading prefix with the row above has no rule under it.

```Java
      PrettyTable grouped = new PrettyTable("Col1", "Col2", "Col3", "Col4");
      grouped.addRow("A", "B", "C", "D");
      grouped.addRow("A", "B", "C", "Z");
      grouped.addRow("A", "B", "F", "E");
      grouped.addRow("A", "B", "F", "E");
      grouped.setRowDividers(true);
```

```
+------+------+------+------+
| Col1 | Col2 | Col3 | Col4 |
+------+------+------+------+
|  A   |  B   |  C   |  D   |
|      |      |      |  Z   |
|      |      |  F   |  E   |
|      |      |      |      |
+------+------+------+------+
```

`setRemoveRedundant(false)` keeps a rule under every data row:

```Java
      grouped.setRemoveRedundant(false);
```

```
+------+------+------+------+
| Col1 | Col2 | Col3 | Col4 |
+------+------+------+------+
|  A   |  B   |  C   |  D   |
+------+------+------+------+
|  A   |  B   |  C   |  Z   |
+------+------+------+------+
|  A   |  B   |  F   |  E   |
+------+------+------+------+
|  A   |  B   |  F   |  E   |
+------+------+------+------+
```

`PrettyTable::setCaption(String)` prints a caption under the table. A `null` or empty caption is skipped. Newlines in the caption are kept, and a caption longer than the table is printed unchanged.

```Java
      table.setCaption("US presidents");
```

```
+------------+----------+------------+
|  Column A  | Column 2 | Column III |
+------------+----------+------------+
|     A      |    B     |     c      |
|     1      |   1024   |   65535    |
| Washington |  Adams   | Jefferson  |
+------------+----------+------------+
US presidents
```

### Deduplication
Both ASCII **text** and **html** formats remove redundancy by default. This de-duplication generally makes the output more aesthetically pleasing, as demonstrated below.

 A table created like this:
 ```Java
    PrettyTable table = new PrettyTable("Col1", "Col2", "Col3", "Col4");
    table.addRow("A", "B", "C", "D");
    table.addRow("A", "B", "C", "Z");
    table.addRow("A", "B", "F", "E");
    
    System.out.println(table.formattedString(OutputFormat.TEXT));
 ```
 produces output that looks like:
 ```
 +------+------+------+------+
| Col1 | Col2 | Col3 | Col4 |
+------+------+------+------+
|  A   |  B   |  C   |  D   |
|      |      |      |  Z   |
|      |      |  F   |  E   |
+------+------+------+------+
 ```
By default, the code recognizes the redundant fields and removes them from the rendering of the data. `PrettyTable::setRemoveRedundant(false)` keeps every repeated value for `toString()` and for `formattedString(OutputFormat)`. One call can still override that setting with `formattedString(OutputFormat, boolean)`. **csv** and **json** keep every cell either way.

In this case, either of these calls:
```Java
   table.setRemoveRedundant(false);
   System.out.println(table.toString());
```
```Java
   System.out.println(table.formattedString(OutputFormat.TEXT, false));
```
produces output that looks like:
 ```
 +------+------+------+------+
| Col1 | Col2 | Col3 | Col4 |
+------+------+------+------+
|  A   |  B   |  C   |  D   |
|  A   |  B   |  C   |  Z   |
|  A   |  B   |  F   |  E   |
+------+------+------+------+
 ```

### Sorting
The tables can be sorted by any of the columns. The sorting can be done by header value, or by column indices. The sorting changes the rows, so it must be done after populating the table to be effective. The sorting functions allow specifing multiple headers or indices to avoid non-deterministic when a chosen column matches.

The sorting looks like this:
```Java
    PrettyTable table = new PrettyTable("Col1", "Col2", "Col3", "Col4");
    table.addRow("A", "B", "C", "D");
    table.addRow("A", "A", "C", "Z");
    table.addRow("A", "C", "F", "E");

    // below sorts are equivalent
    table.sortByIndex(0, 1);
    table.sortByHeader("Col1", "Col2");
```

**NOTE:** specifying an out of range index, or a header that is not found will result in a `ArrayIndexOutOfBoundsException`!

## TableBuilder Usage
`TableBuilder` builds a `PrettyTable` from a `Map` or a `List`. The constructor takes an optional `BuilderConfig`. The data is passed to `build`, which returns the table. `build` returns `null` for `null` or an empty map or list, and throws `IllegalArgumentException` for any other type.

A map becomes a Property / Value table. Nested maps become nested tables, and a list of plain values is joined into one cell. `null` renders as `None`. Booleans render as `True` and `False`.

A list of strings, numbers, or booleans becomes one Items column with a caption such as `Found 6 items`. A list of maps uses the first matching key field (`name`, then `id`) as the row name. One other field becomes the second column. Further fields are nested under Properties. Long keys, values, and URLs are cut off with `...`.

```Java
import rickwporter.prettytable.TableBuilder;

      List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
      Map<String, Object> sna = new LinkedHashMap<String, Object>();
      sna.put("name", "sna");
      sna.put("prop1", 1);
      sna.put("prop B", null);
      sna.put("blah", "zay");
      items.add(sna);

      PrettyTable table = new TableBuilder().build(items);
      System.out.println(table);
```

```
+------+----------------+
| Name | Properties     |
+------+----------------+
| sna  |  prop1   1     |
|      |  prop B  None  |
|      |  blah    zay   |
+------+----------------+
Found 1 items
```

### BuilderConfig
`BuilderConfig` holds the labels, caption, truncation limits, and column selection used by `build`. Each setter returns the config, so calls can be chained. A `null` config uses the defaults.

The caption template replaces `{}`, `%d`, or `%s` with the number of items. `setColumns` chooses columns for a list of maps. A `*` column collects every field that is not named, under the wildcard header (`Properties` by default). An empty column list leaves the default layout in place.

```Java
import rickwporter.prettytable.BuilderConfig;

      BuilderConfig config = new BuilderConfig()
              .setColumns(Arrays.asList("service", "*", "id"))
              .setKeyMaxLen(20)
              .setEllipsis("...");
      PrettyTable table = new TableBuilder(config).build(items);
```

| Setting | Default |
| --- | --- |
| `setItemsLabel` | `Items` |
| `setPropertyLabel` / `setValueLabel` | `Property` / `Value` |
| `setPropertiesLabel` / `setValuesLabel` | `Properties` / `Values` |
| `setUnknownLabel` | `Unknown` |
| `setItemsCaption` | `Found {} items` |
| `setEllipsis` | `...` |
| `setWildcardHeader` | `Properties` |
| `setKeyFields` | `name`, `id` |
| `setKeyMaxLen` / `setValueMaxLen` / `setUrlMaxLen` | 35 / 50 / 100 |
| `setUrlPrefixes` | `http://`, `https://`, `ftp://` |
| `setColumns` | `null` |

## Development

Your input is welcomed.

Please add unit tests for any code that you would like to change. The project has been setup with Jacoco to produce a coverage report in `target/site/jacoco/`.
