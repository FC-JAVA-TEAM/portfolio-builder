# Portfolio Builder

A Spring Boot application for building and managing professional portfolios.

## CI/CD Pipeline

This project uses GitHub Actions for continuous integration and continuous deployment. Here's an overview of our CI/CD process:

### Development Workflow

When working on new features:

1. Create a feature branch from `develop`: `feature/your-feature-name`
2. Make your changes and commit them
3. Push your feature branch
4. Automated processes will:
   - Build the project
   - Run tests
   - Create a development Docker image
   - Deploy to the development environment on Cloud Run

### Production Deployment

Production deployments are triggered by creating a new release tag:

1. Merge your changes into the `develop` branch
2. When ready for a release, create and push a new tag (e.g., `v1.0.0`)
3. The production workflow will automatically:
   - Build and test the application
   - Create a GitHub release
   - Build a production Docker image
   - Deploy to the production environment on Cloud Run

## Required Repository Secrets

The following secrets need to be configured in GitHub repository settings:

### Docker Hub Secrets
- `DOCKER_USERNAME`: Docker Hub username
- `DOCKER_PASSWORD`: Docker Hub password

### Google Cloud Platform Secrets (Development)
- `DEV_GCP_PROJECT_ID`: Development Google Cloud project ID
- `DEV_GCP_SA_KEY`: Development service account key JSON
- `DEV_GCP_REGION`: Development Cloud Run region

### Google Cloud Platform Secrets (Production)
- `PROD_GCP_PROJECT_ID`: Production Google Cloud project ID
- `PROD_GCP_SA_KEY`: Production service account key JSON
- `PROD_GCP_REGION`: Production Cloud Run region

## Development Guidelines

1. Branch Naming:
   - Features: `feature/your-feature-name`
   - Bugfixes: `bugfix/issue-description`
   - Hotfixes: `hotfix/issue-description`

2. Commit Messages:
   - Use clear, descriptive commit messages
   - Reference issue numbers when applicable

3. Pull Requests:
   - Create pull requests to merge your changes into `develop`
   - Ensure all automated checks pass
   - Request reviews from team members

4. Version Naming:
   - Follow semantic versioning (MAJOR.MINOR.PATCH)
   - Prefix with 'v' (e.g., v1.0.0)

## Local Development

1. Clone the repository
2. Install dependencies: `mvn install`
3. Run tests: `mvn test`
4. Start local server: `mvn spring-boot:run`

## Cloud Run Configuration

The application is deployed to Cloud Run with the following configuration:

### Development Environment
- Service Name: portfolio-builder-dev
- Memory: 512Mi
- CPU: 1
- Min Instances: 0
- Max Instances: 2

### Production Environment
- Service Name: portfolio-builder-prod
- Memory: 1Gi
- CPU: 1
- Min Instances: 1
- Max Instances: 10

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request to the `develop` branch

## License

[Add your license information here]
