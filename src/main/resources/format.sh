#!/bin/sh

echo "target directory: $1"
echo "before: $2"
echo "after: $3"

for i in `ls $1`
do
echo "sed -i -e 's#$2#$3#g' $1$i" 
sed -i -e 's#'$2'#'$3'#g' $1$i
done
