Now, to test in Postman:

1. Create a new POST request to `http://localhost:8080/api/v1/send-email`
2. Select "form-data" instead of raw JSON
3. Add the following key-value pairs:
   - to: recipient@example.com
   - subject: Test Subject
   - body: Test Body
   - attachment: Select File (make sure to select the "File" type from the dropdown next to the key)

This setup allows you to:
- Send simple emails without attachments by omitting the attachment field
- Send emails with attachments by including a file in the form-data
- Handle the multipart form data properly in Spring Boot
- Maintain proper validation and error handling

The controller now accepts multipart form data instead of JSON, making it easier to handle file uploads through Postman or any other client.
