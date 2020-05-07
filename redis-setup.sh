#! /bin/bash
docker run -d -p 6377:6379 redis
docker run -d -p 6378:6379 redis
docker run -d -p 6379:6379 redis
