# Encoding Words with Suffixes

This Java program encodes and decodes text files by replacing words and suffixes with numeric codes based on a user-provided mapping file. It’s useful for simple text compression using custom dictionaries.

---

## Features

- Load a CSV file mapping words and suffixes to numbers.
- Encode text files by converting words to their codes.
- Decode encoded files back into text.
- Option to show a progress bar during processing.
- Simple menu-driven interface for easy use.

---

## How to Use

Run the program in **Eclipse IDE** and follow the menu:

1. Load the mapping CSV file (word, suffix and number pairs).
2. Load the text file you want to encode.
3. Set the output filename (default is `./out.txt`).
4. Turn the progress bar on or off.
5. Encode the text file.
6. Decode the encoded file.
7. Quit the program.

---

## Technical Details

- Uses `TreeMap` and `ArrayList` for storing mappings and text.
- Encodes by first checking for exact words, then tries splitting into prefix and suffix parts.
- Words not found in the mapping get encoded as `0`.
- Decoding reverses the mapping to rebuild the original text.
- Includes a simple progress bar shown in the console.
- Well-structured, easy-to-follow Java code with comments.

---

## Performance

- Encoding can be slower for large files due to prefix-suffix checks (up to O(n²) in some cases).
- Decoding is faster, mainly simple lookups.
- Progress bar can be disabled for quicker processing.

---

## Environment

- Developed and tested in **Eclipse IDE**.
- Requires **Java 17** or newer.
