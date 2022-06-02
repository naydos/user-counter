### UI
http://localhost:8080/uploadFile.html
> Read line by line

http://localhost:8080/uploadFile.html
> Read by thread

### Endpoints

localhost:8080/upload
> Read line by line

localhost:8080//upload-csv
> Read by thread

> I used BufferReader to read the file line by line. 
> Seeing that the result took a long time, 
> I decided to use thread. 
> While I thought it was more advantageous to use thread, 
> I realized that it runs slower.I have used executor service to overcome this,
> but it is not resolved. In order not to keep you waiting any longer, 
> I added both solutions.

