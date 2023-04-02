# prettytable-java

![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)

Display data in a tabular form with Java

The data can be output in several different different forms:
* **text** - attractive ASCII format
* **csv** - comma separated values format
* **html** - HyperText Markup Lnaguage (HTML) format
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
import com.rickwporter.prettytable.PrettyTable;
import com.rickwporter.prettytable.PrettyTable.OutputFormat;

      PrettyTable table = new PrettyTable("Column A", "Column 2", "Column III");
      table.addRow("A", "B", "c");
      table.addRow(1, 1024, 65535);
      table.addRow("Washington", "Adams", "Jefferson");

      System.out.println(table.formattedString(OutputFormat.TEXT));
```

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
import com.rickwporter.prettytable.PrettyTable.HorizontalAlign;

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

 ### Deduplication
 Both ASCII **text** and **html** formats remove redundancy by defaults. This de-duplication generally makes the output more aesthetically pleasing, as demonstrated below.

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
 By default, the code recognizes the redundant fields and removes them from the rendering of the data. However, you can print the full table using the `formatString()` method that allows specifying not to de-duplicate the data.
 
 In this case, using this code:
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
