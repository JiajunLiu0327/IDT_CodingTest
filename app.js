const express = require('express')
const app =express()
const mongoClient = require('mongodb').MongoClient

const url = "mongodb://localhost:27017" 
app.use(express.json())

mongoClient.connect(url,(err,db)=>{
	if(err){
		console.log("Error while connecting Mongo Client")
	}
	else{
		const myDb = db.db('myDb')
		const collection = myDb.collection('myTable')

		app.post('/setHistory',(req, res)=>{
			const newLoc = {
				loc: req.body.loc,
				timestamp: req.body.timestamp
			}
			collection.insertOne(newLoc,(err,result)=>{
				if(err) {
        			console.log('Error occurred while inserting');
        		}
        		else{
        			console.log('inserted record', result.ops[0]);
        			res.status(200).send() 
        		}
				
			})

		})

		app.post('/getHistory',(req,res)=>{
			 collection.find( {} ,(err,result)=>{
			 	if (result!=null){
			 		const objToSend ={
			 			loc: result.loc,
			 			timestamp: result.timestamp
			 		}
			 		res.status(200).send(JSON.stringify(objToSend))
			 	}
			 	else{
			 		res.status(404).send()
			 	}

			 })
		})


	}
})



app.listen(3000,()=>{
 	console.log("listening on port 3000...")   
 })
 