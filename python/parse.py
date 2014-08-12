#!/usr/bin/env python3
import sys
import gzip
import http.client
import gzip
from urllib.parse import urlparse

def getNode(nodeString):
    
    def type():
        if nodeString[0] == "\"":
            return "literal"
        if nodeString.find("<http://lodlaundromat.org/.well-known") == 0:
            return "bnode"
        return "uri"
    
    def toString():
        return nodeString
    def value():
        if nodeString[0] == "<":
            return nodeString[1:-1]
        return nodeString[1:nodeString.rfind("\"")]
    def dataType():
        if nodeString[0] == "\"":
            typeIndex = nodeString.rfind("^", nodeString.rfind("\""))
            if typeIndex > 0:
                return nodeString[typeIndex+2:-1]#this removes the < and > from datatype as well
        return None
    def lang():
        if nodeString[0] == "\"":
            langIndex = nodeString.rfind("@", nodeString.rfind("\""))
            if langIndex > 0:
                return nodeString[langIndex+1:]
        return None
                          
    return {
        "type": type,
        "toString": toString,
        "value": value,
        "dataType": dataType,
        "lang": lang
    }
    
def doSomethingWithTriple(triple):
    #Your code here
    print("=============================")
    print("subject: ")
    print("\ttoString: " + triple["sub"]["toString"]())
    print("\tvalue: " + triple["sub"]["value"]())
    print("\ttype: " + triple["sub"]["type"]())
    print("predicate:")
    print("\ttoString: " + triple["pred"]["toString"]())
    print("\tvalue: " + triple["pred"]["value"]())
    print("\ttype: " + triple["pred"]["type"]())
    print("object:")
    print("\ttoString: " + triple["obj"]["toString"]())
    print("\tvalue: " + triple["obj"]["value"]())
    print("\ttype: " + triple["obj"]["type"]())
    dataType = triple["obj"]["dataType"]()
    if dataType == None:
        dataType = "none"
    print("\tdata type: " + dataType)
    lang = triple["obj"]["lang"]()
    if lang == None:
        lang = "none"
    print("\tlang: " + lang)

def getTriple(tripleString):
    triple = {}
    #at which points of this string do the predicate and object start?
    startOfPred = tripleString.find(' ')+1
    startOfObj = tripleString.find(' ', startOfPred)+1
    #take indexes above to return the nodes in this triple
    triple["sub"] = getNode(tripleString[:startOfPred-1])
    triple["pred"] = getNode(tripleString[startOfPred:startOfObj-1])
    triple["obj"] = getNode(tripleString[startOfObj:-3])#remove final ' .' as well
    return triple

if __name__ == "__main__":
    print (len(sys.argv))
    if len(sys.argv) <= 1:
        print("Please pass the gzip data source (either URL or file) as argument")
        sys.exit(1);
    
    unzipper = None;
    if sys.argv[1].startswith("http"):
        conn = http.client.HTTPConnection(urlparse(sys.argv[1]).netloc)
        req = conn.request('GET', urlparse(sys.argv[1]).path)
        resp = conn.getresponse()
        unzipper = gzip.GzipFile(fileobj=resp)
    else:
        unzipper = gzip.open(sys.argv[1], 'r')
        
    for line in unzipper:
        doSomethingWithTriple(getTriple(str(line, encoding='utf8')));
