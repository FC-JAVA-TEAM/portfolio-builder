# Flexi Profile Application Overview

## 1. Introduction
The Flexi Profile application is a robust profile management system that allows users to create and manage their professional profiles. With the new job integration features, it evolves into a comprehensive career management platform that connects professionals with job opportunities.

## 2. Existing Architecture
```mermaid
graph TD
    subgraph Frontend
        Client[Client Application]
    end
    subgraph Backend
        subgraph Controllers
            AC[AuthController]
            PC[ProfileController]
            SC[SectionController]
            HC[HealthController]
            ADC[AdminController]
        end
        subgraph Services
            AS[AuthService]
            PS[ProfileService]
            AUS[AuditService]
            RTS[RefreshTokenService]
        end
        subgraph Security
            JWT[JwtTokenProvider]
            AF[JwtAuthenticationFilter]
            TB[TokenBlacklist]
            TCT[TokenCleanupTask]
        end
        subgraph Models
            U[User]
            P[Profile]
            S[Section]
            SS[SubSection]
            RT[RefreshToken]
            AL[AuditLog]
        end
        subgraph Repositories
            UR[UserRepository]
            PR[ProfileRepository]
            SR[SectionRepository]
            SSR[SubSectionRepository]
            RTR[RefreshTokenRepository]
            ALR[AuditLogRepository]
        end
    end
    subgraph Database
        DB[(MySQL/PostgreSQL)]
    end
    Client --> Controllers
    Controllers --> Services
    Services --> Repositories
    Repositories --> DB
    Controllers --> Security
    Services --> Models
```

### 2.1 Detailed Component Interactions
```mermaid
sequenceDiagram
    participant C as Client
    participant AC as AuthController
    participant PC as ProfileController
    participant AS as AuthService
    participant PS as ProfileService
    participant JWT as JwtTokenProvider
    participant R as Repositories
    participant DB as Database

    C->>AC: Register/Login
    AC->>AS: Authenticate
    AS->>JWT: Generate Token
    JWT-->>AS: Token
    AS-->>AC: Auth Response
    AC-->>C: Token

    C->>PC: Create Profile
    PC->>JWT: Validate Token
    JWT-->>PC: Valid
    PC->>PS: Create Profile
    PS->>R: Save Profile
    R->>DB: Insert
    DB-->>R: Confirm
    R-->>PS: Profile Created
    PS-->>PC: Profile DTO
    PC-->>C: Profile Created Response
```

### 2.1 Existing Components
1. **Authentication & Authorization**
   - JWT-based authentication
   - Role-based access control
   - Token refresh mechanism
   - Token blacklisting

2. **Profile Management**
   - Profile creation and updates
   - Section management
   - Subsection organization
   - Public/Private visibility

3. **Audit & Security**
   - Action logging
   - Token cleanup
   - Admin controls

### 2.2 Existing API Endpoints

#### Authentication Endpoints
```
POST   /api/auth/register     - Register new user
POST   /api/auth/login        - User login
POST   /api/auth/refresh      - Refresh token
POST   /api/auth/logout       - User logout
GET    /api/auth/status       - Check auth status
```

#### Profile Endpoints
```
POST   /api/profiles          - Create profile
GET    /api/profiles/{id}     - Get profile
PUT    /api/profiles/{id}     - Update profile
DELETE /api/profiles/{id}     - Delete profile
GET    /api/profiles          - List all profiles
```

#### Section Endpoints
```
POST   /api/sections                    - Create section
GET    /api/sections/{id}               - Get section
PUT    /api/sections/{id}               - Update section
DELETE /api/sections/{id}               - Delete section
GET    /api/sections/profile/{id}       - Get profile sections
POST   /api/sections/reorder/{id}       - Reorder sections
```

#### Admin Endpoints
```
POST   /api/admin/tokens/revoke/{id}    - Revoke user tokens
GET    /api/admin/tokens/cleanup        - Cleanup expired tokens
POST   /api/admin/tokens/revoke-all     - Revoke all tokens
```

## 3. New Job Integration Architecture
```mermaid
graph TD
    subgraph Profile App
        P[Profile Service]
        JS[Job Search Service]
        RS[Resume Service]
        NS[Notification Service]
        SM[Skills Management]
    end
    subgraph External Services
        Indeed[Indeed API]
        LinkedIn[LinkedIn API]
        Glassdoor[Glassdoor API]
        ATS[ATS Systems]
    end
    subgraph New Features
        JM[Job Matching]
        RA[Resume Analytics]
        AS[Application Status]
        SK[Skills Keywords]
    end
    P --> JS
    P --> RS
    P --> NS
    P --> SM
    JS --> Indeed
    JS --> LinkedIn
    JS --> Glassdoor
    RS --> ATS
    JS --> JM
    RS --> RA
    P --> AS
    SM --> SK
```

### 3.1 Job Search Flow
```mermaid
sequenceDiagram
    participant U as User
    participant PS as Profile Service
    participant JS as Job Search Service
    participant ES as External Services
    participant NS as Notification Service

    U->>PS: Update Profile
    PS->>JS: Trigger Job Search
    JS->>ES: Query Jobs
    ES-->>JS: Job Listings
    JS->>JS: Match Jobs
    JS-->>PS: Matched Jobs
    PS->>NS: Create Job Alerts
    NS-->>U: Job Notifications
    U->>JS: Apply for Job
    JS->>ES: Submit Application
    ES-->>JS: Application Status
    JS-->>U: Confirmation
```

### 3.1 New Components
1. **Job Search Integration**
   - Multi-platform job search
   - Intelligent job matching
   - Application tracking
   - Job recommendations

2. **Resume Management**
   - Resume parsing
   - Skills extraction
   - Format optimization
   - ATS compatibility check

3. **Skills Management**
   - Skills assessment
   - Endorsements
   - Gap analysis
   - Skill recommendations

4. **Notifications**
   - Job alerts
   - Application updates
   - Interview reminders
   - Profile optimization suggestions

### 3.2 New API Endpoints

#### Job Search Endpoints
```
GET    /api/jobs/search              - Search jobs across platforms
GET    /api/jobs/match               - Get matching jobs
POST   /api/jobs/preferences         - Set job preferences
GET    /api/jobs/recommendations     - Get job recommendations
```

#### Application Tracking Endpoints
```
POST   /api/applications             - Create application
GET    /api/applications/{id}        - Get application
PUT    /api/applications/{id}        - Update application
DELETE /api/applications/{id}        - Delete application
GET    /api/applications/status      - Get application status
```

#### Skills Management Endpoints
```
POST   /api/skills                   - Add skill
GET    /api/skills/{id}             - Get skill
PUT    /api/skills/{id}             - Update skill
DELETE /api/skills/{id}             - Delete skill
POST   /api/skills/endorse/{id}     - Endorse skill
GET    /api/skills/analysis         - Get skills analysis
```

#### Resume Management Endpoints
```
POST   /api/resume/parse            - Parse resume
GET    /api/resume/optimize         - Get optimization suggestions
POST   /api/resume/format           - Format resume
GET    /api/resume/ats-check        - Check ATS compatibility
```

## 4. Complete Entity Relationship Diagram
```mermaid
erDiagram
    User ||--o{ Profile : has
    Profile ||--o{ Section : contains
    Section ||--o{ SubSection : has
    User ||--o{ RefreshToken : has
    User ||--o{ AuditLog : generates
    Profile ||--o{ JobApplication : tracks
    Profile ||--o{ Skill : contains

    User {
        Long id
        String email
        String password
        String firstName
        String lastName
        boolean isActive
        DateTime createdAt
        DateTime updatedAt
    }
    Profile {
        Long id
        Long userId
        String name
        String bio
        boolean isPublic
    }
    Section {
        Long id
        Long profileId
        String type
        String title
        int order
        String backgroundUrl
    }
    SubSection {
        Long id
        Long sectionId
        String title
        String content
        int order
    }
    RefreshToken {
        Long id
        Long userId
        String token
        DateTime expiryDate
    }
    AuditLog {
        Long id
        Long userId
        String action
        DateTime timestamp
        String details
    }
    JobApplication {
        Long id
        Long profileId
        String jobId
        String company
        String position
        String status
        DateTime appliedDate
        String source
    }
    Skill {
        Long id
        Long profileId
        String name
        String level
        Boolean isVerified
        Integer endorsements
    }
```

## 5. Implementation Steps

### Phase 1: Foundation
1. Set up new models
   - Create JobApplication entity
   - Create Skill entity
2. Create repositories
   - JobApplicationRepository
   - SkillRepository
3. Configure external API connections
   - Indeed API integration
   - LinkedIn API integration
   - Glassdoor API integration

### Phase 2: Core Services
4. Implement new services
   - JobSearchService
   - ResumeService
   - SkillsService
   - NotificationService
5. Develop controllers
   - JobController
   - ApplicationController
   - SkillController
   - ResumeController

### Phase 3: Features
6. Implement job matching algorithm
   - Skills matching
   - Experience matching
   - Location matching
   - Salary range matching
7. Develop resume features
   - Resume parsing
   - Format optimization
   - ATS compatibility checking
8. Set up notification system
   - Email notifications
   - In-app notifications
   - Job alerts

### Phase 4: Integration & Testing
9. Update existing services
   - Extend ProfileService
   - Enhance SecurityConfig
10. Write tests
    - Unit tests
    - Integration tests
    - E2E tests
11. Update documentation
    - API documentation
    - Integration guides
    - User guides

### Phase 5: Deployment
12. Deployment preparation
    - Environment configuration
    - Database migration
    - API key management
13. Monitoring setup
    - Performance monitoring
    - Error tracking
    - Usage analytics
