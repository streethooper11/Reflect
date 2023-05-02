# CPSC501F22A4



## About the assignment

The assignment has 2 parts:
1. Create objects, serialize information about the object, and send the serialized data over the socket
2. Receive data over the socket, deserialize the data, and visualize the result

## Content

SenderSerializerMain is the program for serializing and sending data.\
ReceiverDeserializerMain is the program for receiving, deserializing, and visualizing data.

When executing SenderSerializerMain, an optional parameter may be given, which
is the port number the program will bind to.\
When executing Receiver, there are 2 optional parameters:
1. IP address to connect to
2. port number

The default port number is 50150 for both programs, and
Receiver has 127.0.0.1 (localhost) for default IP address.\
The program assumes the correct format for IP address (X.X.X.X) 
is provided by the user.

## How to use the program

There are 6 kinds of objects, chosen in the menu by its corresponding number:
1. Object with 2 instance variable of type int
2. Object that has Object 1
3. Object with an array of variables of type int
4. Object with an array of Object 1s
5. Object with an HashSet of Object 1s
6. Object with circular reference (ClassCircularOne has reference to ClassCircularTwo, 
and ClassCircularTwo has reference to ClassCircularOne)

There are 2 additional options:
7. Switch between JSON and XML (default JSON)
8. Quit the program

User input is required to set the values:\
Option 3 will ask the user to indicate length, then put a series of ints depending on length.
Option 4 will ask the user to indicate length, then for each element, 2 inputs will be required.

SenderSerializerMain must be executed with option (1-6) first, then ReceiverDeserializerMain must be executed
after Sender class starts listening.

## Bonus

XML was implemented as a bonus. Depending on the mode, the sender program will either
call Serializer for json or SerializerXML for xml then 
prepend a character depending on the mode.\
("J" for json mode, "X" for xml mode).

When the receiver program receives data, it first checks the first character, and depending on its result
("J" or "X"), it will either call Deserializer for json, or DeserializerXML for xml.

## Testing

On sender side, testing was mostly done to check if a string obtained 
using ObjectCreator into Serializer matches a string obtained 
directly from Serializer matches.

After testing was done on the sender side, testing was done on the receiver side
if data transfer is properly done.\
Afterwards, it was checked if an object created before serialization into deserialization
was equal to an object obtained after serialization into deserialization,
using hashcode.

## Code Smells and Refactoring

### First Refactoring

The first code smell, long method, was detected right before tests were about to be created.\
Extract method was done for code blocks in each if condition, then another Extract method was done
for common code blocks in each created method.

### Second Refactoring

As sender.Serializer was developed, code was difficult to understand as there had to be many conditions,
and it was becoming hard to catch errors, especially when dealing with Collection class.\
Extract method was done to break down the serializeHelper method, which helped catch errors.

### Further Refactoring

Further Refactoring were Extract methods to prevent long method and duplicate codes.

