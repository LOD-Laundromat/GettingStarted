#!/usr/bin/env node

var zlib = require('zlib'),
	request = require('request'),
	readline = require('readline'),
	fs = require('fs'),
	devnull = require('dev-null');



var getNodeHandlers = function(node) {
	return {
		type: function() {
			if (node.charAt(0) == "\"") return "literal";
			if (node.indexOf("<http://lodlaundromat.org/.well-known") == 0) return "bnode";
			return "uri";
			
		},
		toString: function() {
			return node;
		},
		value: function() {
			if (node.charAt(0) == "<") return node.slice(1, -1);
			
			//it is a literal
			return node.slice(1, node.lastIndexOf("\""));
		},
		dataType: function() {
			if (node.charAt(0) == "\"") {
				var typeIndex = node.lastIndexOf("^", node.length - node.lastIndexOf("\""));
				if (typeIndex > 0) return node.slice(typeIndex + 2, -1);//this removes the < and > from datatype as well
			}
			return null;
		},
		lang: function() {
			if (node.charAt(0) == "\"") {
				var langIndex = node.lastIndexOf("@", node.length - node.lastIndexOf("\""));
				if (langIndex > 0) return node.slice(langIndex + 1);
			}
			return null;
		}
		
	}
};
var parseTriple = function(triple) {
	var 
		//at which points of this string do the predicate and object start?
		startPred = triple.indexOf(" ")+1, 
		startObj = triple.indexOf(" ", startPred)+1,
		//take indexes above to return the nodes in this triple
		sub = triple.slice(0, startPred-1),
		pred = triple.slice(startPred, startObj-1),
		obj = triple.slice(startObj, -2);//remove final ' .' as well
	return {
		sub: getNodeHandlers(sub),
		pred: getNodeHandlers(pred),
		obj: getNodeHandlers(obj),
	}
};

if (process.argv.length <= 2) {
	console.log("Please pass the gzip data source (either URL or file) as argument")
}
var rl = readline.createInterface({
	input: (process.argv[2].indexOf("http") == 0? request(process.argv[2]): fs.createReadStream(process.argv[2])).pipe(zlib.createUnzip()),
	output: devnull()
}).on('line', function(line) {
	doSomethingWithTriple(parseTriple(line));
	rl.close();
});

var doSomethingWithTriple = function(triple) {
	//Your code here
	console.log("=============================");
	console.log("subject: ");
	console.log("\ttoString: " + triple.sub.toString());
	console.log("\tvalue: " + triple.sub.value());
	console.log("\ttype: " + triple.sub.type());
	console.log("predicate:");
	console.log("\ttoString: " + triple.pred.toString());
	console.log("\tvalue: " + triple.pred.value());
	console.log("\ttype: " + triple.pred.type());
	console.log("object:");
	console.log("\ttoString: " + triple.obj.toString());
	console.log("\tvalue: " + triple.obj.value());
	console.log("\ttype: " + triple.obj.type());
	console.log("\tdata type: " + triple.obj.dataType());
	console.log("\tlang: " + triple.obj.lang());
};

//node
//java
//python
//php (optional)