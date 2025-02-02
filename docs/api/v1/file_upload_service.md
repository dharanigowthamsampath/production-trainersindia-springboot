1. Upload a file:
```bash
POST /api/v1/files/upload
Content-Type: multipart/form-data
file: <your-file>
```

2. Download a file:
```bash
GET /api/v1/files/{fileName}
```

3. Delete a file:
```bash
DELETE /api/v1/files/{fileName}
```

Features of this implementation:
- Secure file naming using UUID
- File extension preservation
- Configurable upload directory
- Configurable base URL for file access
- Error handling
- Logging
- Support for large files
- File download support
- File deletion capability
- Clean architecture with separation of concerns

The files will be stored in the configured upload directory and can be accessed via the generated URL. The URL will be in the format: `http://localhost:8080/api/v1/files/{fileName}`.

Remember to:
1. Add the uploads directory to .gitignore
2. Create the uploads directory if it doesn't exist
3. Ensure proper file permissions on the uploads directory
4. Consider implementing additional security measures like file type validation
5. Consider implementing cloud storage integration for production use
