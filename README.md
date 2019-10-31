## Kafka set up in K8s

* [Spring support for kafka](https://spring.io/projects/spring-kafka)
* [Spring Kafka simple producer consumer example](https://github.com/spring-projects/spring-kafka/tree/master/samples/sample-01)
* [Spring Kafka Multi-Method Listener](https://github.com/spring-projects/spring-kafka/tree/master/samples/sample-02)
* [Spring Kafka Transactional](https://github.com/spring-projects/spring-kafka/tree/master/samples/sample-02)
* [Helm Chart for Kubernetes](https://github.com/confluentinc/cp-helm-charts/tree/master/charts/cp-kafka)
* [Bitnami Kafka Helm Chart](https://github.com/bitnami/charts/tree/master/bitnami/kafka/#installing-the-chart)

## Status 

Got it working from producer to consumer 

```sh

% curl -X POST http://localhost:8080/send/cache/foo //run 7 times

% curl http://localhost:8081/consumer/counts       
7

```

## Todo

* Use TLS
* Create helm charts for install
* Automate minikube set up (script it)
* Use JSON payload instead of Path for spring controllers 
* Install health checks for Kafka for Spring Boot
* Install health checks for each uService 
* Add Spring Boot ENV var overrides 
* Install Prometheus support for health 


## ZBB todo 
* Switch to async model (may not be needed)
