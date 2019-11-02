## Automation scripts and perhaps helm will live here maybe


Before running Spring (minikube, k8s, and Kafka set up is covered below).


```
 minikube start -p k8s-kafka --kubernetes-version v1.15.4 --vm-driver=hyperkit \
   --cpus=4 --disk-size='100000mb' --memory='6000mb'

```

```
kubectl port-forward --namespace default svc/kafka-cluster 9092:9092
```


----


### Running end to end 
```sh

curl -X POST \
   -H "Content-Type: application/json" \
   -d '{"timestamp":"1572654520446", "key":"foo"}' \
   http://localhost:8080/send/cache      

## See if consumer got the post 
curl http://localhost:8081/consumer/counts/success      

1                                                                                                                                                                                              
## See if cache CDN stub was called by consumer 
% curl http://localhost:8082/cache/invalidate/count       

1

```





We attempted to use https://github.com/confluentinc/cp-helm-charts.git but it seemed too beefy for our simple use case. 

The rather small Minikube environment becomes overwhelmed and never comes back after that install so we deleted it rahter than spend too many cycles tyring to debug it.
We may try again especially if we end us using Avro and the schema registry. 



We tried this Kafka manager helm chart but realized that  [kafka manager helm](https://github.com/helm/charts/tree/master/stable/kafka-manager) 
is [Kafka manager](https://github.com/yahoo/kafka-manager) is not a kafka cluster install but a manager of Kafka clusters. 


#### Install minikube with right amount of memory, diskspace and correct kubernetes version for helm
```sh
% minikube start -p k8s-kafka --kubernetes-version v1.15.4 --vm-driver=hyperkit \
   --cpus=4 --disk-size='100000mb' --memory='6000mb'
```

#### Output
```sh
😄  [k8s-kafka] minikube v1.4.0 on Darwin 10.15
🔥  Creating hyperkit VM (CPUs=4, Memory=6000MB, Disk=100000MB) ...
🐳  Preparing Kubernetes v1.15.4 on Docker 18.09.9 ...
🚜  Pulling images ...
🚀  Launching Kubernetes ... 
⌛  Waiting for: apiserver proxy etcd scheduler controller dns
🏄  Done! kubectl is now configured to use "k8s-kafka"
```

Note this time, I ran it with a name `k8s-kafka`. 

Now reinstall helm into your new cluster. 

```sh
helm init --history-max 200
```

If you don't start up minikube with the above args, it will auto upgrade to the latest version of K8s and then 
helm 2.x charts no longer works. 



## Bitnami Kafka Helm chart

This [Kafka Helm chart from Bitnami](https://bitnami.com/stack/kafka/helm) looks promising. 



> VMware has acquired Bitnami to accelerate application delivery for multi-cloud and Kubernetes and expand Bitnami 
> adoption in the enterprise. Learn more by reading the official announcement and the post from our founders 
> --[Bitnami](https://bitnami.com/)



#### Add the bitnami helm repo to your list of repos 
```sh
(⎈ |k8s-kafka:default) helm % helm repo add bitnami https://charts.bitnami.com/bitnami

"bitnami" has been added to your repositories
```

Read the [Bitnami Kafka Helm Chart](https://github.com/bitnami/charts/tree/master/bitnami/kafka/#installing-the-chart) documentation. 

```sh

% helm install --name kafka-cluster bitnami/kafka
```

#### Kafka Helm Instructions from helm install above

```sh 
% helm install --name kafka-cluster bitnami/kafka
NAME:   kafka-cluster
LAST DEPLOYED: Tue Oct 29 13:33:43 2019
NAMESPACE: default
STATUS: DEPLOYED

RESOURCES:
==> v1/Pod(related)
NAME                       READY  STATUS   RESTARTS  AGE
kafka-cluster-0            0/1    Pending  0         0s
kafka-cluster-zookeeper-0  0/1    Pending  0         0s

==> v1/Service
NAME                              TYPE       CLUSTER-IP     EXTERNAL-IP  PORT(S)                     AGE
kafka-cluster                     ClusterIP  10.102.208.81  <none>       9092/TCP                    1s
kafka-cluster-headless            ClusterIP  None           <none>       9092/TCP                    1s
kafka-cluster-zookeeper           ClusterIP  10.96.226.49   <none>       2181/TCP,2888/TCP,3888/TCP  1s
kafka-cluster-zookeeper-headless  ClusterIP  None           <none>       2181/TCP,2888/TCP,3888/TCP  1s

==> v1/StatefulSet
NAME                     READY  AGE
kafka-cluster            0/1    1s
kafka-cluster-zookeeper  0/1    1s


NOTES:


** Please be patient while the chart is being deployed **

Kafka can be accessed via port 9092 on the following DNS name from within your cluster:

    kafka-cluster.default.svc.cluster.local

To create a topic run the following command:

    export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=kafka,app.kubernetes.io/instance=kafka-cluster,app.kubernetes.io/component=kafka" -o jsonpath="{.items[0].metadata.name}")
    kubectl --namespace default exec -it $POD_NAME -- kafka-topics.sh --create --zookeeper kafka-cluster-zookeeper:2181 --replication-factor 1 --partitions 1 --topic test

To list all the topics run the following command:

    export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=kafka,app.kubernetes.io/instance=kafka-cluster,app.kubernetes.io/component=kafka" -o jsonpath="{.items[0].metadata.name}")
    kubectl --namespace default exec -it $POD_NAME -- kafka-topics.sh --list --zookeeper kafka-cluster-zookeeper:2181

To start a kafka producer run the following command:

    export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=kafka,app.kubernetes.io/instance=kafka-cluster,app.kubernetes.io/component=kafka" -o jsonpath="{.items[0].metadata.name}")
    kubectl --namespace default exec -it $POD_NAME -- kafka-console-producer.sh --broker-list localhost:9092 --topic test

To start a kafka consumer run the following command:

    export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=kafka,app.kubernetes.io/instance=kafka-cluster,app.kubernetes.io/component=kafka" -o jsonpath="{.items[0].metadata.name}")
    kubectl --namespace default exec -it $POD_NAME -- kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning

To connect to your Kafka server from outside the cluster execute the following commands:

    kubectl port-forward --namespace default svc/kafka-cluster 9092:9092 &
    echo "Kafka Broker Endpoint: 127.0.0.1:9092"

    PRODUCER:
        kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic test
    CONSUMER:
        kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic test --from-beginning


```


#### K8s: Let's see which context we are using

```sh

%  kubectl config get-contexts
CURRENT   NAME        CLUSTER     AUTHINFO    NAMESPACE
*         k8s-kafka   k8s-kafka   k8s-kafka   
          minikube    minikube    minikube    
          sys         minikube                kube-system

```

Perfect.

## Test the Kafka cluster running in K8s

After the install you can test that the cluster works with the Kafka command line utils. 



#### K8s: Watch kubernetes come up with kubectl watch

```sh
%  kubectl get pods  --watch

NAME                        READY   STATUS    RESTARTS   AGE
kafka-cluster-0             1/1     Running   0          13m
kafka-cluster-zookeeper-0   1/1     Running   0          13m
```

#### K8s: Get the name of the kafka pod

```
% export KAFKA_POD_NAME=$(kubectl get pods --namespace default \
  -l "app.kubernetes.io/name=kafka,app.kubernetes.io/instance=kafka-cluster,app.kubernetes.io/component=kafka" \
  -o jsonpath="{.items[0].metadata.name}")

% echo $KAFKA_POD_NAME
kafka-cluster-0


```

To create a topic run the following commands. You will have to use this export a few times.

#### K8s Kafka: Create a test topic

```sh

% kubens default

Context "k8s-kafka" modified.
Active namespace is "default".

% kubectl  exec -it $KAFKA_POD_NAME -- kafka-topics.sh \
  --create --zookeeper kafka-cluster-zookeeper:2181 \
  --replication-factor 1 --partitions 1 \
  --topic test

Created topic test.  

```


To list all the Kafka topics run the following command.

#### K8s Kafka: List the topics

```sh

% kubectl exec -it $KAFKA_POD_NAME \
 -- kafka-topics.sh --list \
 --zookeeper kafka-cluster-zookeeper:2181

test
```

Notice our test topic. 

In a new terminal tab, to start a Kafka producer run the following command (be sure to set up `KAFKA_POD_NAME` as above).

#### K8s Kafka: Start a producer


```sh

% kubens default

Context "k8s-kafka" modified.
Active namespace is "default".

% kubectl exec -it $KAFKA_POD_NAME \
     -- kafka-console-producer.sh --broker-list localhost:9092 \
     --topic test
```

In a different terminal window, to start a kafka consumer run the following command (again be sure to set up `KAFKA_POD_NAME` as above).

#### K8s Kafka: Start a consumer  

```sh

% kubens default

Context "k8s-kafka" modified.
Active namespace is "default".

% kubectl exec -it $KAFKA_POD_NAME -- kafka-console-consumer.sh \
     --bootstrap-server localhost:9092 --topic test --from-beginning

```

 
If the command line tools worked, then you have a working kafka cluster running in K8s. 

