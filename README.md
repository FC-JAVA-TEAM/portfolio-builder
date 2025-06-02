# Portfolio Builder API

A flexible API for building and managing professional portfolios.

## CI/CD Pipeline

The project uses GitHub Actions for continuous integration and deployment, with Docker for containerization.

### Development Workflow

1. Push to feature branches (`feature/*`) or develop branch triggers:
   - Build and test
   - Docker image creation and push to Docker Hub
   - Deployment to development environment

2. Development Environment:
   - Hosted on Google Cloud Run
   - Minimal resource allocation (512Mi memory, 1 CPU)
   - Auto-scaling from 0 to 2 instances

### Production Workflow

1. Creating a version tag (e.g., `v1.2.3`) triggers:
   - Build and test
   - Docker image creation and push to Docker Hub
   - GitHub Release creation
   - Deployment to production environment

2. Production Environment:
   - Hosted on Google Cloud Run
   - Higher resource allocation (1Gi memory, 1 CPU)
   - Auto-scaling from 1 to 10 instances
   - Production-grade configuration

### Docker Images

Images are stored on Docker Hub with the following tags:
- Latest commit: `<username>/portfolio-builder:<commit-sha>`
- Latest version: `<username>/portfolio-builder:latest`
- Release versions: `<username>/portfolio-builder:v*.*.*`

## Development Setup

1. Prerequisites:
   - Java 17
   - Maven
   - Docker

2. Build the application:
   ```bash
   mvn clean package
   ```

3. Build Docker image:
   ```bash
   docker build -t portfolio-builder .
   ```

4. Run locally:
   ```bash
   docker run -p 2222:2222 portfolio-builder
   ```

## Environment Variables

The application uses Spring profiles for different environments:
- `development`: Development environment settings
- `production`: Production environment settings
- `test`: Test environment settings

## API Documentation

[Add API documentation here]

## Contributing

1. Create a feature branch from develop
2. Make your changes
3. Submit a pull request
4. CI will automatically build and test your changes

## License

[Add license information here]
