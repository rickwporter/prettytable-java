# prettytable-java
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
The ASCII **text** output can have different alignments. Currently, **text** is the only output format that can support the alignments. Here's how you would update the above table:
```Java
import com.rickwporter.prettytable.PrettyTable.CellFormat;

      table.setFormats(CellFormat.LEFT, CellFormat.CENTER, CellFormat.RIGHT);
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

 ### Deduplication
 The ASCII **text** output has de-duplication for more aesthetically pleasing output. Currently, **text*** is the only format where this de-duplication is supported, and there is no option to turn it off.
  
## Development

Your input is welcomed.
