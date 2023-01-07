# FallChallenge2022 - KeepOffTheGrass

Keep Off The Grass!

Source code for CodinGame's Fall Challenge 2022 event.

https://www.codingame.com/contests/fall-challenge-2022/

Community starter AIs are located here:

https://github.com/CodinGame/FallChallenge2022-KeepOffTheGrass/tree/main/starterAIs

## Build and run locally

How to build and run the referee locally.

### Prerequites

#### Java 8

Java 8 or higher: The pom.xml file specifies that the maven-compiler-plugin should use a source and target compatibility of 1.8, which means that the code will be compiled to be compatible with Java 8 or higher. You will need to have a Java 8 or higher runtime installed to run the code. See https://openjdk.java.net/ to install.

```console
java -version
```

##### Install OpenJDK:

Download the OpenJDK 8 or higher for the user's platform from the OpenJDK website: https://openjdk.java.net/

Follow the installation instructions provided by OpenJDK to install the JDK on the user's system. This will typically involve extracting the downloaded archive and setting the JAVA_HOME environment variable to point to the installation directory.

After the installation is complete, the user should verify that the OpenJDK is correctly installed by running the java -version command and checking the output. The output should indicate that the OpenJDK is being used (e.g. "OpenJDK 11.0.9").

#### Apache Maven

Apache Maven 3.5.3 or higher: The pom.xml file is a Maven project file, and you will need Maven 3.5.3 or higher installed to build and run the project. Maven is a build automation tool that is used to manage the dependencies, build process, and testing of a Java project.

```console
mvn -version
```
##### Maven install instructions

Download Apache Maven 3.5.3 or higher from the Apache Maven website: https://maven.apache.org/download.cgi

Extract the downloaded archive to a directory on the user's system. This will create a directory with the name "apache-maven-3.5.3" (or similar, depending on the version you downloaded).

Set the MAVEN_HOME environment variable to point to the directory where Maven was installed. This will allow Maven and other tools to find Maven when they need to use it. The user can set the MAVEN_HOME variable by following these steps:

On Windows:
Right-click on the "Computer" icon and select "Properties"
Click on the "Advanced system settings" link
Click on the "Environment Variables" button
Under "System Variables", scroll down and click on the "New" button
In the "Variable name" field, enter "MAVEN_HOME"
In the "Variable value" field, enter the path to the Maven installation directory (e.g. "C:\Program Files\Apache Maven 3.5.3")
Click the "OK" button to close the "New System Variable" dialog
Click the "OK" button to close the "Environment Variables" dialog
Click the "OK" button to close the "System Properties"
On macOS or Linux:
Open a terminal window and enter the following command:
export MAVEN_HOME=path/to/maven/installation/directory
Replace "path/to/maven/installation/directory" with the actual path to the Maven installation directory (e.g. "/usr/local/maven")
Add the Maven bin directory to the user's PATH environment variable. This will allow the user to run Maven from any directory by simply typing "mvn" on the command line. The user can add the bin directory to the PATH variable by following these steps:

On Windows:
Follow the steps 1-3 above to open the "Environment Variables" dialog
Under "System Variables", scroll down and find the "Path" variable
Click on the "Edit"

#### Node and npm

Node.js is a runtime environment for running JavaScript applications, and npm is the package manager for Node.js. If you do not have these tools installed, you can install them from the Node.js website: https://nodejs.org/

### Build Javascript

```console
cd .\src\main\resources\view\
npm install
npm run start
```

To build the Javascript part of the project, you can instruct the user to follow these steps:

Open a terminal window and navigate to the src/main/resource/view directory. This is the directory where the Javascript source code is located.

Run the npm install command to install the required dependencies for the project. This will install the packages listed in the package.json file, which includes any libraries or tools that the project needs to build and run.

Run the npm run start command to build and start the project. This will use the scripts defined in the package.json file to build the project and start a development server. The development server will automatically reload the project when changes are made to the source code, which can be useful for testing and debugging.

### CLI

#### Build and run

To build the project and create an executable jar file, run the following command in the root directory of the project:

```console
mvn clean package
```

#### Execute the jar

To run the game with two bots and a specified seed, use the following command in Powershell:

```Powershell
$bot1 = "path/to/bot1"
$bot2 = "path/to/bot2"
$seed = "seed of the map"

Start-Process "java" "-jar target/fall-challenge-2022-keep-off-the-grass-1.0-SNAPSHOT.jar $bot1 $bot2 $seed" -Wait
```
