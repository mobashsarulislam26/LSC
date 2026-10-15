# Dynamic Camp Configuration, Role-Based Experience & Advanced Camp Management

A comprehensive upgrade making all camp operational parameters (stations, activities, schedule timings, groups, rooms, competitions, scoring criteria, and meal sessions) fully dynamic and editable in real-time by authorized officials, paired with role-tailored login dashboards, an Admin Control Center, Live Bulletin broadcasts, an Incident & Lost-and-Found registry, and automated PDF Participant Certificate generation.

## User Review & Critical Decisions

> [!IMPORTANT]
> The user confirmed the following functional scope:
> 1. **Dynamic Camp Customization (All 5 domains confirmed)**: Camp stations & moderators, Daily schedule events & rotation timing, Camp groups & room/bed allotments, Competition events & scoring criteria, and Daily meal distribution menus & timings.
> 2. **Advanced New Capabilities (All 4 modules confirmed)**: Centralized Admin Control Panel, Live Camp Announcement Bulletin board with push priority flags, Incident & Lost-and-Found tracking log, and Participant Certificate Generator with printable preview and PDF/share export.
> 3. **Role-Based Authentication**: Enhanced login interface enabling users to authenticate with their assigned camp role (Admin, Station Moderator, Group Leader, Judge, Medical Officer, Logistics/Food, Volunteer) and immediately land on a role-customized dashboard tailored to their duties.

- **Confirmed Decision 1**: All camp configuration is stored in the local Room SQLite database with full CRUD (Create, Read, Update, Delete) capability so any station, schedule time, room assignment, or meal menu can be modified on the fly without updating app code or resetting data.
- **Confirmed Decision 2**: Super Admin and authorized coordinators have a dedicated "Admin Control Center" modal and management views with safety locks to dynamically add/edit/remove camp entities anytime.
- **Confirmed Decision 3**: The existing strict login gate ("login ছাড়া ব্যবহার করা যাবে না") is maintained and enhanced to grant role-specific permissions and shortcuts immediately upon login.

---

## 1. Overview & Core Concept

### What It Does
1. **Fully Dynamic Operations**: Eliminates hardcoded camp structures. Coordinators can add new activity stations, alter schedule slots, adjust meal distribution counters, modify judging criteria, and assign/move participants between groups and rooms with instant persistence.
2. **Role-Tailored Login & Views**: Staff log in with their email and role-specific password. The app dynamically adapts the primary landing view to their role (e.g. Station Moderators see their station attendance scanner, Medical staff see the triage alert dashboard, Judges see scoring sheets, Food officers see meal distribution counters).
3. **Admin Control Panel**: A unified administrative hub where coordinators can manage all dynamic camp settings, export database backups, generate reports, and manage staff accounts.
4. **Live Bulletin & Announcements**: A centralized notice board with priority levels (Urgent/Emergency, General, Daily Highlights) that can be posted and dismissed by coordinators.
5. **Incident & Lost and Found Tracking**: Real-time logging of camp incidents (injuries, property issues, disputes) and Lost & Found items with status tracking (Reported, In Progress, Resolved/Returned).
6. **Certificate Generator & PDF Export**: Produces high-resolution, beautifully formatted graduation and achievement certificates with student name, camp ID, group, honors/awards, and camp director signatures that can be viewed and exported as PDF/image.

---

## 2. User Experience & Visual Design

### Aesthetic Direction & Visual Identity
- **Palette**: Deep Emerald (`#064E3B`, `#059669`), Slate Navy (`#0F172A`, `#1E293B`), Warm Gold Accent (`#D97706`, `#F59E0B`), and Clean Pure White Card surfaces with subtle border strokes (`#E2E8F0`).
- **Typography**: Clean, readable typography in both Bengali and English, with high-contrast badge indicators for roles, urgent alerts, and status tags.
- **Density & Touch**: Standard 48dp minimum touch targets, floating quick-action buttons, intuitive swipe/dialog editors, and clear visual hierarchy.

### Key User Flows

```
[Staff Login / Sign-up]
   │
   ├──> Authenticate with Email + Password (received via email)
   │
   ├──> Select / Verify Camp Role (Admin, Station Mod, Judge, Medical, Logistics)
   │
   ▼
[Role-Customized Landing Experience]
   ├── Super Admin ──> Full Control Panel + Global Metrics + All Modules
   ├── Station Moderator ──> Direct Station Check-in Scanner + Station Rosters
   ├── Group Leader ──> Group Roster + Cabin/Room Check + Attendance Overview
   ├── Competition Judge ──> Live Scoring Sheets + Group Standings
   ├── Medical Officer ──> Triage Logs + Medication Schedules + Health Alerts
   └── Logistics / Food ──> Meal Distribution Counters + Dietary Restrictions
```

### New Screens & Components
1. **Dynamic Entity Management Dialogs**:
   - `ManageStationsDialog`: Add/Edit station name, moderator, location, and icon.
   - `ManageScheduleDialog`: Add/Edit daily schedule slots, days, and event descriptions.
   - `ManageRoomsDialog`: Add/Edit rooms, capacities, and assigned campers.
   - `ManageMealsDialog`: Add/Edit meal sessions, menu items, and distribution windows.
2. **Admin Control Center Screen**:
   - Consolidated tabbed management for all 5 dynamic domains, staff accounts, system settings, and data exports.
3. **Camp Bulletin Board**:
   - Cards showing active announcements, pinned emergency alerts, filter by audience, and 'New Notice' creation tool.
4. **Incident & Lost & Found Hub**:
   - Log item or incident with date, location, finder/reporter, photo note, and resolution status toggle.
5. **Certificate Generator**:
   - Customizable certificate template with live preview, recipient selection (individual or whole group), award badges, and Android Print/PDF share intent.

---

## 3. Key Product Decisions & Trade-Offs

### Decision 1: Room DB Schema Extension for Full Dynamic CRUD
- **Chosen Approach**: Extend `CampDao` and SQLite tables (`stations`, `attendance_events`, `schedule_events`, `rooms`, `meal_distributions`, `competitions`, `bulletin_notices`, `incident_reports`) with full `@Insert`, `@Update`, and `@Delete` operations.
- **Why**: Allows instant dynamic updates across the entire app without requiring schema migrations that wipe user data. Ensures full offline capability so camp staff can modify schedules or stations without active internet.

### Decision 2: Role-Driven Navigation & Permission Security
- **Chosen Approach**: Retain the strict login gate while enriching `UserAccountEntity` and `CampViewModel` with active role permissions. Non-admin roles see a focused dashboard optimized for their station or task, while Admins retain global unlock capability.
- **Why**: Keeps the UI simple and fast for volunteers while giving coordinators complete power.

### Decision 3: Zero-Dependency Android Print & PDF Certificate Generation
- **Chosen Approach**: Implement standard Android `PrintManager` and Compose Canvas / Bitmap rendering for certificates, exporting to PDF/PNG share intents via `FileProvider`.
- **Why**: Zero external bloated third-party PDF dependencies; 100% native compatibility on all Android versions.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────┐
│                    Jetpack Compose UI                   │
│  ┌──────────────────────┐    ┌──────────────────────┐  │
│  │   Role-Based Login   │    │  Admin Control Panel │  │
│  │   & Sign-Up Screen   │    │  (Dynamic Settings)  │  │
│  └──────────┬───────────┘    └──────────┬───────────┘  │
│             │                           │              │
│  ┌──────────▼───────────────────────────▼───────────┐  │
│  │  Modules: Stations, Schedule, Rooms, Meals,      │  │
│  │  Competitions, Bulletin, Incidents, Certificates │  │
│  └──────────────────────────┬───────────────────────┘  │
└─────────────────────────────┼──────────────────────────┘
                              │
┌─────────────────────────────▼──────────────────────────┐
│                   CampViewModel (StateFlow)            │
│  - currentRole & currentUser                           │
│  - dynamicStations & dynamicSchedules                  │
│  - dynamicRooms & dynamicMeals                         │
│  - dynamicCompetitions & bulletinNotices               │
│  - incidentReports & certificateState                  │
└─────────────────────────────┬──────────────────────────┘
                              │
┌─────────────────────────────▼──────────────────────────┐
│             CampRepository & Room Database             │
│  ┌──────────────────────────────────────────────────┐  │
│  │  CampDao (Full CRUD on all operational entities) │  │
│  │  - stations, schedule_events, attendance_events  │  │
│  │  - rooms, meal_distributions, competitions       │  │
│  │  - bulletin_notices, incident_reports            │  │
│  │  - user_accounts (Role-based auth & tokens)      │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────┘
```

### Implementation Steps
1. **Extend Models & Room DAO**:
   - Add `BulletinNoticeEntity` and `IncidentReportEntity` to `CampModels.kt`.
   - Add CRUD methods in `CampDao.kt` for stations, schedules, rooms, meals, competitions, notices, and incidents.
2. **Update Repository & ViewModel**:
   - Expose dynamic StateFlows and mutation methods (`createStation`, `updateStation`, `deleteStation`, `createScheduleEvent`, `updateScheduleEvent`, `createRoom`, `updateMeal`, `addBulletinNotice`, `addIncidentReport`).
   - Add role-aware routing logic on login.
3. **Build Admin Control Panel & Dynamic Editors**:
   - Create editor dialogs for stations, schedules, rooms, meals, and competitions.
   - Build the unified Admin Control Center tab.
4. **Implement Bulletin & Incident Hub**:
   - Build the interactive Announcement board and Incident / Lost & Found screens.
5. **Build Certificate Generator Component**:
   - Design the printable certificate template with PDF export and direct sharing.
6. **Verification & Tests**:
   - Run unit tests covering dynamic CRUD operations, role switching, notice broadcasts, and compilation checks.
