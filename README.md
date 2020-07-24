# PromoTube

PromoTube is a browser-based web application which extracts promotional codes and affiliate links from YouTube video descriptions.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

This app requires Maven. To install Maven run

```
sudo apt install maven
```
This app requires at least JDK 8. To install JDK 8 or higher run
```
sudo apt install openjdk-8-jre-headless
```
or
```
sudo apt install default-jdk 
```
Note: These instructions are for Linux systems.

## Running the Tests


### Unit Testing

To run unit tests run
```
mvn test
```

To run end to end tests run
```
mvn package appengine:run
```

## Deployment

To deploy the app on a server run

```
mvn package appengine:deploy
```
with the proper project ID in the pom.xml file.

To deploy locally run
```
mvn package appengine:run
```

## Built With

* [BootStrap 4](https://getbootstrap.com/docs/4.0/getting-started/introduction/) - The CSS framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [jQuery](https://api.jquery.com/) - Javascript Library
* [YouTube Data API v3](https://developers.google.com/youtube/v3) - YouTube API

## Authors

* **Dante Allen** - *Initial work* - [dallen68](https://github.com/dallen68)
* **Margaret Chan** - *Initial work* - [margaretchan](https://github.com/margaretchan)
* **Josef Jankowski** - *Initial work* - [josefj1519](https://github.com/josefj1519)

See also the list of [contributors](https://github.com/dallen68/PromoTube/contributors) who participated in this project.

## License

This project is licensed under the Apache 2 Licenses - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

* We would like to thank our hosts Sam Li and Derrill Dabkoski who guided us through this process.
