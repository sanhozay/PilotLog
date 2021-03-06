[![GPL3](https://img.shields.io/badge/license-GPL3-%23a42e2b.svg)](https://www.gnu.org/licenses/gpl-3.0.en.html)
[![Travis branch](https://img.shields.io/travis/sanhozay/PilotLog/master.svg?label=master)](https://travis-ci.org/sanhozay/PilotLog)
[![Travis branch](https://img.shields.io/travis/sanhozay/PilotLog/develop.svg?label=develop)](https://travis-ci.org/sanhozay/PilotLog)

# PilotLog

PilotLog is a simple flight logging service for Flightgear. It collects data as
Flightgear runs and provides a web application that is viewable through your web
browser.

Each time you take off, a new flight will be entered into the flight record.
When you land, arrival details will be added to the flight, along with various
computed values, e.g. flight duration and fuel used.

If you pause Flightgear (p) or speed up the simulation (a/A) at any time between
takeoff and landing, the active flight will be invalidated. It will also be
invalidated on arrival if the total fuel is greater than or equal to the fuel at
takeoff, or if the flight time was less than one minute. Incomplete and invalid
flights are removed when a new flight starts.

The web interface provides searching by callsign, aircraft, origin and
destination.

In addition to the web interface, flight details can also be downloaded in XML
or CSV format. The latter is easily imported into a spreadsheet.

## Prerequistes

PilotLog requires Flightgear 2017.1.0 or later because prior versions do not
handle the response status line sent by Tomcat server.

To run the service, you will need a Java 14 runtime environment.

If you are running a Linux distribution, your package manager may have a Java 14
runtime environment that you can install.

You can check your Java version in a terminal or command window, for example:

    $ java -version
    openjdk version "14.0.2" 2020-07-14
    OpenJDK Runtime Environment (build 14.0.2+12-46)
    OpenJDK 64-Bit Server VM (build 14.0.2+12-46, mixed mode, sharing)

You can download a Java runtime environment from [Open JDK](http://openjdk.java.net).

If you want to build the web service, you will need a Java SDK such as
[Open JDK](http://openjdk.java.net).

## Simple Usage

### Installation

1. Download a release distribution from <https://github.com/sanhozay/PilotLog/releases>.
2. Create a `Nasal` directory in `$FG_HOME` if necessary.
3. Copy `pilotlog.nas` to `$FG_HOME/Nasal`.

### Starting the service

1. Run the executable JAR file, e.g. `PilotLog-2.0.0.jar`, from a terminal or
   command window
2. Check the service is running at <http://localhost:8080>

The service must be running to record your flights so you may want to arrange
for it to run when your computer starts or when you login.

### Stopping the service

1. Press Ctrl-C in the terminal or kill the process.

### Uninstallation

1. Delete `pilotlog.nas` from `$FG_HOME/Nasal`.
2. Delete the database files in `$HOME/.h2/`.
3. Delete the directory where you unpacked the release distribution.

## Advanced Usage

### Remote installation

You can run the service on a different machine from Flightgear, as long as the
machine running Flightgear can access the other machine over a network.

1. Start the service on the remote machine as described above.

2. Edit `$FG_HOME/Nasal/pilotlog.nas` and change the hostname variable to the
   hostname of the machine where you are running the service.

### Building the project

1. Use the Gradle build wrapper `$ ./gradlew build`
2. The executable JAR is created in `build/libs`.

### Web service endpoints

The main endpoints are as follows:

1. <http://localhost:8080/api/flights.json> (all flights as a JSON response)
2. <http://localhost:8080/api/flights.xml> (all flights as an XML response)
3. <http://localhost:8080/api/flights.csv> (all flights as a CSV response)

### Further information

The application is built using Spring Boot. Information on installing the
application as a Linux daemon or Windows service can be found here:

[Installing Spring Boot
Applications](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html)
