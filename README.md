# Verteilte Anwendungen Übungsaufgabe 4

Mögliche Punktzahl: 20 Punkte

## Deadlines

- 1. Zug: 9.7.2025
- 2. Zug: 8.7.2025 

## Aufgabenstellung
In dieser Aufgabe erhalten Sie kein vollständig vorkonfiguriertes Project; 
dafür jedoch ein existierendes Docker Compose File. 
Dieses Docker Compose File fährt bereits alle benötigten Komponenten für Sie hoch.
Um sich einen Überblick über die Server-Instanzen zu verschaffen, empfiehlt es sich einen Blick in die [docker-compose.yml](docker-compose.yml) zu werfen.

Mit dieser Applikation können Sie
auf ein Chat zugreifen und sich die neusten 
Nachrichten anzeigen lassen. Hierzu müssen sie lediglich 
ihre Applikation starten, um anschließend auf den 
VA LinkedIn Channel unter [http://localhost:8081](http://localhost:8081)
zugreifen zu können.
Leider sind diese Informationen nur generiert und auch nur lokal 
auf einer Instanz verfügbar.
Das Ziel dieser Aufgabe ist die Bereitstellung der LinkedIn Nachrichten auf zwei oder mehr Instanzen.
Die Herausforderung besteht somit darin, die Nachrichten nach Kafka zu schreiben bzw. 
aus Kafka zu lesen und somit auf allen Instanzen ihrer Applikation 
gleichermaßen darzustellen.

1.  **(4P)** Konfigurieren Sie ein Kafka Topic namens "chat".
Dieses Topic muss mindestens **zwei** Partitionen besitzen!
Entscheiden Sie sich für eine geeignete Retention und begründen Sie Ihre
Entscheidung bei der Projektabgabe.
Konfigurieren Sie das von Ihnen erzeugte Topic in der
[application.properties](src/main/resources/application.properties).
2.  **(6P)** Implementieren Sie die Consumer und Producer Funktionalität
für das Erzeugen und Verarbeiten von Nachrichten.
Da es sich hierbei um eine Schnittstelle ihrer Applikation handelt,
sollten sich auch die entsprechenden Klassen im 
[Boundary Package](src/main/java/de/berlin/htw/boundary)
befinden.

Mit den ersten beiden Aufgaben haben Sie ein Publish/Subscribe Pattern
implementiert, bei dem mehrere Consumer sämtliche Nachrichten
empfangen und verarbeiten. Ein weiteres Anwendungsgebiet ist das 
sogenannte Job Processing,
bei dem Kafka als Job Queue eingesetzt werden kann. Mit den folgenden Aufgaben
wollen wir unsere Applikation um eine endlose Berechnung der
Fibonacci-Folge erweitern. Hierbei soll immer nur **ein** Consumer
ein Fibonacci Tuple aus Kafka lesen, 
dann das nächste Fibonacci Tuple berechnen und anschließend
wieder nach Kafka schreiben. Beachten Sie bitte hierbei, dass die 
Reihenfolge der Fibonacci Tuple im Stream entscheidend ist!

3.  **(4P)** Konfigurieren Sie ein Kafka Topic namens "fibonacci".
Entscheiden Sie sich für eine geeignete Retention, eine sinnvolle
Anzahl an Partitionen und wählen Sie die maximal mögliche Anzahl an Replikationen.
Begründen Sie Ihre Entscheidung bei der Projektabgabe.
Vervollständigen Sie die Konfiguration entsprechend dem erzeugten Topic in der
[application.properties](src/main/resources/application.properties).
4.  **(6P)** Implementieren Sie die Consumer und Producer Funktionalität zur
endlosen Berechnung der Fibonacci-Folge. 

Weitere Informationen zur Implementierung von Kafka in 
Quarkus finden sie auf der Quarkus Webseite: 
[Apache Kafka Reference Guide](https://quarkus.io/guides/kafka)

Hilfreiche Informationen zur Konfiguration des Kafka Clients
finden sie auf der SmallRye Webseite:
[Incoming Attributes of the Kafka Connector](https://smallrye.io/smallrye-reactive-messaging/smallrye-reactive-messaging/3.1/kafka/kafka.html#_configuration_reference) 

# Konfigurationsbegründungen

## Chat-Topic Konfiguration
- **Partitionen**: 2
  - Begründung: Zwei Partitionen ermöglichen eine bessere Lastverteilung zwischen den Consumern, während die Komplexität durch zu viele Partitionen vermieden wird.
  
- **Retention**: 7 Tage (604800000 ms)
  - Begründung: Chat-Nachrichten haben einen gewissen historischen Wert, aber nach einer Woche sind sie in der Regel nicht mehr relevant. Dies verhindert auch, dass das Topic unbegrenzt wächst.

## Fibonacci-Topic Konfiguration
- **Partitionen**: 1
  - Begründung: Da die Reihenfolge der Nachrichten für die korrekte Berechnung der Fibonacci-Folge kritisch ist, wird nur eine einzelne Partition verwendet. Dies stellt sicher, dass alle Nachrichten in der richtigen Reihenfolge verarbeitet werden.
  
- **Replikate**: 3
  - Begründung: Drei Replikate bieten eine gute Balance zwischen Verfügbarkeit und Speicherverbrauch. Diese Konfiguration ermöglicht den Ausfall von bis zu zwei Brokern, ohne dass Daten verloren gehen oder die Verfügbarkeit beeinträchtigt wird.
  
- **Retention**: 1 Stunde (3600000 ms)
  - Begründung: Da die Fibonacci-Berechnung eine kontinuierliche Kette ist und alte Berechnungsschritte nicht mehr benötigt werden, ist eine kurze Retention-Zeit ausreichend. Dies verhindert auch, dass das Topic unbegrenzt wächst.

# Kafka Get Started

Da es leider für [Apache Kafka](https://kafka.apache.org/) kein
offizielles Image auf Docker Hub gibt,
erhalten Sie in dieser Aufgabe ein Docker Compose File
mit einer Referenz-Installation.

Zum Starten des Kafka Clusters können Sie Docker Compose im Detached Modus ausführen:
```shell script
$docker-compose up -d
```
> **_Achtung:_**  Diese Referenz-Installation stellt ein Kafka Management UI bereit, das Sie über Ihren Browser unter http://localhost:8080/ erreichen können.

# Quarkus Get Started

Dieses Projekt nutzt Quarkus, das Supersonic Subatomic Java Framework.
Da wir in dieser Aufgabe zwei Instanzen der Quarkus Applikation parallel
betreiben wollen und immer nur ein Port von einem Prozess verwendet
werden kann, wurden zwei Profile konfiguriert:
- primary
- secondary

Wenn sie mehr über die Profil-Konfiguration wissen wollen,
dann besuchen Sie bitte die Quarkus Webseite:
[Configuration Reference Guide](https://quarkus.io/guides/config-reference) .

## Running the application in dev mode

Sie können die primäre Instanz Ihrer Applikation mit dem folgenden Kommando
im dev Mode starten:
```shell script
mvn compile quarkus:dev -D"quarkus.http.port=8081" -D"quarkus.profile=primary"
```

```shell script
mvn compile quarkus:dev -D"quarkus.http.port=8082" -D"quarkus.profile=secondary"
```

> **_Achtung:_**  Quarkus wird im dev Mode mit einer Dev UI ausgeliefert,
die Sie entweder unter http://localhost:8081/q/dev/ oder unter
http://localhost:8082/q/dev/ erreichen können.

## Packaging and running the application

The application can be packaged using:
```shell script
$ mvn package
```
It produces the `verteilte-anwendung-runner.jar` file in the `target/` directory.

The primary application instance is now runnable using 
`$ java  -Dquarkus.profile=primary -jar target/verteilte-anwendung-runner.jar`wh
and the secondary by using
`$ java  -Dquarkus.profile=secondary -jar target/verteilte-anwendung-runner.jar`.

mvn compile quarkus:dev -D"quarkus.http.port=8081" -D"quarkus.profile=primary"
mvn compile quarkus:dev -D"quarkus.http.port=8082" -D"quarkus.profile=secondary"