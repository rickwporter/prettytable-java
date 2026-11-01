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

## Usage
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
ASCII **text** output can hide the header row, drop the border, and print a caption under the table. **csv**, **html**, and **json** ignore these three settings.

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

## Development

Your input is welcomed.

Please add unit tests for any code that you would like to change. The project has been setup with Jacoco to produce a coverage report in `target/site/jacoco/`.
