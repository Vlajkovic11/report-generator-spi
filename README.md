This project implements reusable and extensible software components for generating reports in multiple formats (CSV, PDF, custom TXT).  
The architecture follows the **Service Provider Interface (SPI)** pattern, separating API specification from implementations that are loaded at runtime.  
A command-line program demonstrates the use of these libraries, connecting to a database and generating reports with formatting, headers, summaries, and calculations (COUNT, SUM, AVERAGE, etc.)
