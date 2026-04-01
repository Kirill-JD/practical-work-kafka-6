#1. выдача прав админу, брокерам (внутри кластера) и на кластер для продюсера
docker exec -it kafka-0 /bin/kafka-acls --bootstrap-server kafka-0:29092 --add --allow-principal User:CN=kafka-0,L=Moscow,OU=Practice,O=Yandex,C=RU --operation All --cluster --command-config /tmp/adminclient-configs.conf
docker exec -it kafka-0 /bin/kafka-acls --bootstrap-server kafka-0:29092 --add --allow-principal User:CN=kafka-1,L=Moscow,OU=Practice,O=Yandex,C=RU --operation All --cluster --command-config /tmp/adminclient-configs.conf
docker exec -it kafka-0 /bin/kafka-acls --bootstrap-server kafka-0:29092 --add --allow-principal User:CN=kafka-2,L=Moscow,OU=Practice,O=Yandex,C=RU --operation All --cluster --command-config /tmp/adminclient-configs.conf
docker exec -it kafka-0 /bin/kafka-acls --bootstrap-server kafka-0:29092 --add --allow-principal User:CN=admin,L=Moscow,OU=Practice,O=Yandex,C=RU --operation All --cluster --command-config /tmp/adminclient-configs.conf
docker exec -it kafka-0 /bin/kafka-acls --bootstrap-server kafka-0:29092 --add --allow-principal User:CN=producer,L=Moscow,OU=Practice,O=Yandex,C=RU --operation ClusterAction --operation Describe --cluster --command-config /tmp/adminclient-configs.conf

# 2. создание топиков
docker exec -it kafka-0 /bin/kafka-topics --bootstrap-server kafka-0:29092 --create --topic topic-1 --partitions 2 --replication-factor 3 --command-config /tmp/adminclient-configs.conf
docker exec -it kafka-0 /bin/kafka-topics --bootstrap-server kafka-0:29092 --create --topic topic-2 --partitions 2 --replication-factor 3 --command-config /tmp/adminclient-configs.conf

# 3. выдача прав продюсеру и консьюмеру на топики
docker exec -it kafka-0 /bin/kafka-acls --bootstrap-server kafka-0:29092 --add --allow-principal User:CN=producer,L=Moscow,OU=Practice,O=Yandex,C=RU --operation WRITE --operation DESCRIBE --topic topic-1 --command-config /tmp/adminclient-configs.conf
docker exec -it kafka-0 /bin/kafka-acls --bootstrap-server kafka-0:29092 --add --allow-principal User:CN=producer,L=Moscow,OU=Practice,O=Yandex,C=RU --operation WRITE --operation DESCRIBE --topic topic-2 --command-config /tmp/adminclient-configs.conf
docker exec -it kafka-0 /bin/kafka-acls --bootstrap-server kafka-0:29092 --add --allow-principal User:CN=consumer,L=Moscow,OU=Practice,O=Yandex,C=RU --operation READ --operation DESCRIBE --topic topic-1 --command-config /tmp/adminclient-configs.conf
