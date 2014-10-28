#!/bin/sh

echo "working directory: $1"
echo "result directory: $2"
echo "scorpio path: $3"
echo "repository URL: $4"
echo "start revision number: $5"
echo "end revision number: $6"
echo "time directory: $7"

if [ ! -e $2 ]
then
echo "mkdir -p $2"
mkdir -p $2
fi

if [ ! -e $7 ]
then
echo "mkdir -p $7"
mkdir -p $7
fi

echo "svn checkout -r $5 $4 $1"
svn checkout -r $5 $4 $1

echo "java -jar -Xmx4g $3 -s 6 -d $1 -t 4 -o $2/scorpio-$5.txt"
{ time java -jar -Xmx4g $3 -s 6 -d $1 -t 4 -o $2/scorpio-$5.txt ;} 1>>$7/scorpio-time-$5.txt 2>&1

for i in `seq $5 $6`
do

echo "svn update -r $i $1"
svn update -r $i $1

echo "java -jar -Xmx4g $3 -s 6 -d $1 -t 4 -o $2/scorpio-$i.txt"
{ time java -jar -Xmx4g $3 -s 6 -d $1 -t 4 -o $2/scorpio-$i.txt ;} 1>>$7/scorpio-time-$i.txt 2>&1

done

