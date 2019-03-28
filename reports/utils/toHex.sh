#!/bin/bash

for f in *.jasper ; do echo -e 'x\c' > $f.hex && cat $f | xxd -pxxd -c 999999999 >> $f.hex; done
