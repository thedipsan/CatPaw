# 🐾 PawMate

> **Your Pet. Our Care.**

PawMate is a modern Android pet-care management application designed to help pet owners organize pets, daily care routines, health information, tasks, expenses, and important pet-care activities in one place.

Built as a native Android application using **Java + XML + Android Studio**.

## ✨ Features

- 🔐 Email/password authentication
- 🔵 Google Sign-In
- 🔢 OTP verification / password recovery
- 🐕 Pet profile management
- ➕ Add, view, edit and delete pets
- 📋 Create and manage care routines
- ✅ Mark care tasks as completed
- 💊 Healthcare and vaccination information
- 💰 Pet expense tracking
- 📩 SMS care-task delegation
- 📍 Pet-care locations / geotagging
- 🔔 Reminders and notifications

> Remove or edit any feature above that is not implemented in your current version.

## 🎨 UI / UX

PawMate uses a clean, modern mobile interface inspired by Material Design.

The design focuses on:

- Simple navigation
- Consistent spacing
- Rounded cards
- Clear typography
- Meaningful icons
- Accessible controls
- Responsive Android layouts

### Main Navigation

```text
Home
 ├── Pets
 ├── Tasks
 ├── Healthcare
 ├── Expenses
 ├── Locations
 └── Profile
```

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java | Android application development |
| XML | User interface layouts |
| Android Studio | Development environment |
| Firebase Authentication | User authentication |
| Cloud Firestore | Application data |
| Firebase Storage | Pet images |
| Material Components | UI components |
| Google Sign-In | Google authentication |
| Android Intent | SMS delegation |
| Google Maps / Location Services | Location functionality |

> Keep only the technologies actually used in your repository.

## 📂 Project Structure

```text
PawMate/
├── app/
│   └── src/main/
│       ├── java/com/example/pawmate/
│       │   ├── activities/
│       │   ├── fragments/
│       │   ├── adapters/
│       │   ├── models/
│       │   └── utils/
│       │
│       └── res/
│           ├── drawable/
│           ├── layout/
│           ├── menu/
│           ├── mipmap/
│           └── values/
│
└── README.md
```

Adjust the package name and folders to match your actual Android Studio project.

## 🔐 Authentication Flow

```text
Splash Screen
      ↓
Authentication Check
      ↓
 ┌────┴─────┐
 ↓          ↓
Logged In  Logged Out
 ↓          ↓
Home       Login
            │
      ┌─────┼──────────┐
      ↓     ↓          ↓
   Google  Password  Register
            │
            ↓
      Forgot Password
            ↓
      OTP Verification
            ↓
       Create Password
            ↓
           Home
```

## 🐾 Pet Management

A pet profile can contain:

- Pet name
- Species
- Breed
- Date of birth
- Weight
- Dietary preference
- Allergies
- Favourite toy
- Vaccination information
- Photo
- Notes

### CRUD

```text
Create → Add Pet
Read   → View Pet
Update → Edit Pet
Delete → Delete Pet
```

## 📋 Care Routine

Users can create routines for:

- Feeding
- Exercise
- Grooming
- Medication
- Healthcare
- Other custom activities

A routine may contain:

```text
Pet
Task Name
Category
Frequency
Time
Required Supplies
Notes
Reminder
Completed
```

## 📩 SMS Delegation

Users can select care tasks and prepare a care message for another person.

```text
Select Tasks
     ↓
Build Care Message
     ↓
SMS Intent
     ↓
Android Messaging App
```

## 📍 Location Feature

Important pet-care places can be saved, such as:

- Veterinary clinics
- Grooming salons
- Dog parks
- Pet stores

Location data may include:

```text
Name
Category
Address
Latitude
Longitude
Notes
```

## 🗄️ Suggested Data Structure

```text
users
└── userId
    ├── name
    ├── email
    │
    ├── pets
    │   └── petId
    │       ├── name
    │       ├── species
    │       ├── breed
    │       ├── weight
    │       ├── diet
    │       ├── allergies
    │       ├── favouriteToy
    │       └── photoUrl
    │
    ├── routines
    │   └── routineId
    │       ├── petId
    │       ├── taskName
    │       ├── category
    │       ├── frequency
    │       ├── time
    │       ├── notes
    │       └── completed
    │
    ├── expenses
    │   └── expenseId
    │
    └── locations
        └── locationId
```

## 🚀 Getting Started

### Prerequisites

- Android Studio
- Android SDK
- Java/JDK compatible with the project's Gradle configuration
- Android emulator or physical Android device

### Clone the repository

```bash
git clone YOUR_GITHUB_REPOSITORY_URL
```

### Open the project

Open the cloned project in Android Studio and allow Gradle to sync.

### Firebase setup

Create/configure the Firebase project used by the application and enable only the services required by your implementation, such as:

- Firebase Authentication
- Google Sign-In
- Cloud Firestore
- Firebase Storage

Add the required Firebase configuration to the Android project.

### Google Sign-In

Configure Google authentication in Firebase and add the required Android signing fingerprints for your development environment.

### OTP

Configure the OTP provider/service used by your implementation.

**Never commit passwords, private API keys, tokens, service-account credentials, or other secrets to GitHub.**

## 🧪 Testing Checklist

### Authentication

- [ ] Registration
- [ ] Login
- [ ] Invalid credentials
- [ ] Google Sign-In
- [ ] Forgot Password
- [ ] OTP verification
- [ ] Create/reset password
- [ ] Logout

### Pets

- [ ] Add pet
- [ ] View pet
- [ ] Edit pet
- [ ] Delete pet
- [ ] Upload/update pet photo

### Routines

- [ ] Create routine
- [ ] View routine
- [ ] Edit routine
- [ ] Delete routine
- [ ] Mark task complete

### Delegation

- [ ] Select tasks
- [ ] Generate message
- [ ] Open SMS application

### Locations

- [ ] Add location
- [ ] Save coordinates
- [ ] Display location/marker

## 📸 Screenshots

Add your actual screenshots here:

```text
docs/
└── screenshots/
    ├── login.png
    ├── register.png
    ├── dashboard.png
    ├── pets.png
    ├── add-pet.png
    ├── tasks.png
    └── profile.png
```

Example:

```markdown
![Login Screen](docs/screenshots/login.png)
```

## 🗺️ Development Roadmap

- [x] Initial project setup
- [ ] UI implementation
- [ ] Authentication
- [ ] Google Sign-In
- [ ] OTP verification
- [ ] Forgot Password
- [ ] Dashboard
- [ ] Pet CRUD
- [ ] Care Routine CRUD
- [ ] Task completion
- [ ] SMS delegation
- [ ] Healthcare
- [ ] Expenses
- [ ] Location/geotagging
- [ ] Notifications
- [ ] Testing
- [ ] Final UI polish

Update these items to reflect the real state of your project.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test the application
5. Commit your changes
6. Push the branch
7. Open a Pull Request

## 📄 License

This project is intended for educational and learning purposes.

If you later publish PawMate as an open-source project, add a license that matches your intended usage.

## 👨‍💻 Developer

**PawMate — Your Pet. Our Care.**

Built with ❤️ using **Java, XML and Android Studio**.
