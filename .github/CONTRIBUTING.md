# Contributing

## Pull Requests

Pull requests are welcome but please contact the project maintainer to discuss your plans before starting significant pieces of work.

## Code Style

Aim to follow the coding style currently in use in the project. Broadly speaking this is based on [Sun's Java coding standards](http://www.oracle.com/technetwork/java/codeconventions-150003.pdf).

If you don't want to read all that: it's 4 spaces for indentation, K&amp;R style. Prefer explict imports over wildcards in Java code and add JavaDoc where it might be useful.

If you use your IDE to format automatically, please ensure that it doesn't make widespread changes to the existing code. If in doubt, turn it off.

## Version control

Commit often but group related changes into a single commit. An ideal commit should represent one idea or step in the process toward implementing a feature.

Commit messages need not be verbose, but please make an effort. Messages like "fixed bug" are not helpful.

The project `.gitignore` file has exclusions for Gradle builds only. If you are using an IDE, please ensure that your global gitignore file is configured to exclude any project files and build directories that it creates.