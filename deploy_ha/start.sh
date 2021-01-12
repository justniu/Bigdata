docker run -d  -p 50070:50070 --net hadoopnet --name hadoop1 --hostname hadoop1 hadoop:3.0
docker run -d  --net hadoopnet --name hadoop2 --hostname hadoop2 hadoop:3.0
docker run -d  --net hadoopnet --name hadoop3 --hostname hadoop3 hadoop:3.0
docker run -d  --net hadoopnet --name hadoop4 --hostname hadoop4 hadoop:3.0
docker run -d  --net hadoopnet --name hadoop5 --hostname hadoop5 hadoop:3.0
docker run -d  --net hadoopnet --name hadoop6 --hostname hadoop6 hadoop:3.0
docker run -d  --net hadoopnet --name hadoop7 --hostname hadoop7 hadoop:3.0
docker run -d  --net hadoopnet --name hadoop11 --hostname hadoop11 -p 8485:8485 hadoop:3.0
docker run -d  --net hadoopnet --name hadoop12 --hostname hadoop12 -p 8486:8485 hadoop:3.0
docker run -d  --net hadoopnet --name hadoop13 --hostname hadoop13 -p 8487:8485 hadoop:3.0
docker-compose -f stack.yml up -d
