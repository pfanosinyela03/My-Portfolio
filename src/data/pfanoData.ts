export interface ProjectItem {
  id: string;
  key: string;
  title: string;
  subtitle: string;
  type: 'apk' | 'web' | 'hybrid';
  category: 'systems' | 'mobile' | 'web';
  categoryLabel: string;
  link: string;
  apkLink?: string;
  buttonLabel: string;
  image: string;
  galleryImages: string[];
  description: string;
  techStack: string[];
  highlights: string[];
}

export interface ExperienceRecord {
  id: string;
  role: string;
  company: string;
  employmentType: string;
  period: string;
  duration: string;
  location: string;
  isCurrent: boolean;
  description: string;
  skills: string[];
}

export interface EducationRecord {
  id: string;
  institution: string;
  qualification: string;
  period: string;
  specialization: string;
  details: string;
}

export const dynamicTitles = [
  'Full Stack Developer',
  'Android Developer',
  'Oracle DBA',
  'Software Engineer',
  'Web Designer',
];

export const movingTools = [
  { name: 'Flutter', category: 'Cross-Platform' },
  { name: 'Java', category: 'Backend & Android' },
  { name: 'Oracle DBA', category: 'Databases' },
  { name: 'Kotlin', category: 'Android Native' },
  { name: 'SQL Databases', category: 'Relational DB' },
  { name: 'Azure DevOps', category: 'CI/CD & Cloud' },
  { name: 'C# / .NET', category: 'Systems' },
  { name: 'Firebase', category: 'Cloud Backend' },
  { name: 'React', category: 'Web Frontend' },
  { name: 'TypeScript', category: 'Type-Safe Web' },
  { name: 'Android SDK', category: 'Mobile Architecture' },
  { name: 'Git & GitHub', category: 'Version Control' },
  { name: 'REST APIs', category: 'Networking' },
  { name: 'SQLite / Room', category: 'Local Storage' },
];

export const pfanoProfile = {
  name: 'Pfano Sinyela',
  tagline: 'Crafting high-quality, scalable mobile, cloud, and full-stack applications.',
  cvUrl: '/assets/cv/Pfano-Sinyela-CV.pdf',
  email: 'sinyelapfano@gmail.com',
  linkedin: 'https://www.linkedin.com/in/pfano-sinyela-408058286/',
  instagram: 'https://www.instagram.com/fano.szn/',
  github: 'https://github.com/pfanosinyela03',
  avatar: '/src/assets/images/avatar_pfano_sinyela_profile_enhanced.png',
  setupImage: '/src/assets/images/developer_workspace_setup_1791996132660.jpg',
  darkLandscapeSetup: '/src/assets/images/dark_landscape_setup_1791996890435.jpg',
  aboutSubtitle: 'Hi, I am Pfano Sinyela 👋',
  aboutText:
    'I am a dedicated Full-Stack Developer with a strong passion for crafting high-quality, scalable applications. With proficiency in Flutter, Kotlin, Java, C#, SQL, HTML/CSS, and various other technologies, I specialize in both front-end and back-end development, enabling me to deliver seamless, efficient solutions across different platforms.',
  aboutHighlight:
    'Let’s connect — I’m always open to new ideas, collaborations or just chatting about tech!',
};

export const experiencesList: ExperienceRecord[] = [
  {
    id: 'exp-1',
    role: 'Software Engineer',
    company: 'Aegar Tech',
    employmentType: 'Contract',
    period: 'Jun 2026 — Present',
    duration: '',
    location: 'City of Cape Town, Western Cape, South Africa · Remote',
    isCurrent: true,
    description:
      'Engineered scalable full-stack applications and cross-platform services. Collaborating with cross-functional teams to deliver maintainable software architectures, performance optimization, and reliable product features.',
    skills: ['Full-Stack Development', 'Mobile Systems', 'TypeScript', 'Clean Code', 'API Design'],
  },
  {
    id: 'exp-2',
    role: 'Oracle Database Administrator',
    company: 'OptiSolutions',
    employmentType: 'Internship',
    period: 'Oct 2025 — May 2026',
    duration: '8 mos',
    location: 'City of Cape Town, Western Cape, South Africa · Remote',
    isCurrent: false,
    description:
      'Managed Oracle database administration, performance monitoring, schema design, and SQL query optimization. Executed database backup and recovery operations ensuring strict data integrity and high availability.',
    skills: ['Oracle DBA', 'SQL Optimization', 'Database Administration', 'Backup & Recovery', 'Data Integrity'],
  },
];

export const educationList: EducationRecord[] = [
  {
    id: 'edu-1',
    institution: 'Rosebank International South Africa',
    qualification: 'Diploma in Software Development, Software development',
    period: '2021 — 2024',
    specialization: 'Software Development',
    details: 'Comprehensive study of object-oriented programming, data structures, mobile native architecture, and relational database systems.',
  },
];

export const skillsList = [
  {
    name: 'Kotlin',
    category: 'Mobile Engineering',
    level: 92,
    description: 'Modern Android architectures, Coroutines, Jetpack Compose, Clean Architecture',
    color: '#7F52FF',
  },
  {
    name: 'Java',
    category: 'Backend & Android',
    level: 90,
    description: 'Enterprise APIs, OOP design patterns, multithreading, Spring Boot & Android SDK',
    color: '#E76F00',
  },
  {
    name: 'C#',
    category: 'Systems & .NET',
    level: 86,
    description: '.NET Core, desktop architectures, entity frameworks, cross-platform services',
    color: '#239120',
  },
  {
    name: 'Web Development',
    category: 'Frontend & Full-Stack',
    level: 94,
    description: 'HTML5, CSS3, modern JavaScript/TypeScript, React, responsive layouts',
    color: '#2563EB',
  },
  {
    name: 'Azure DevOps',
    category: 'Cloud & CI/CD',
    level: 85,
    description: 'Automated build pipelines, containerized deployments, cloud environments',
    color: '#0089D6',
  },
  {
    name: 'SQL & Oracle DBA',
    category: 'Databases & Storage',
    level: 88,
    description: 'Relational schema design, query optimization, indexing, Oracle database administration',
    color: '#F80000',
  },
  {
    name: 'Flutter',
    category: 'Cross-Platform Mobile',
    level: 90,
    description: 'Responsive cross-platform apps, reusable widgets, state management, and API integration',
    color: '#02569B',
  },
  {
    name: 'JavaScript',
    category: 'Frontend Development',
    level: 85,
    description: 'Modern ES6+, interactive interfaces, asynchronous programming, and web application logic',
    color: '#F7DF1E',
  },
  {
    name: 'Firebase',
    category: 'Backend & Cloud',
    level: 90,
    description: 'Authentication, Cloud Firestore, real-time data, cloud storage, and application integrations',
    color: '#FFCA28',
  },
];

export const projectsList: ProjectItem[] = [
  {
    id: 'avitrek',
    key: 'avitrek',
    title: 'AviTrek',
    subtitle: 'Wildlife Birdwatching & GPS Field Guide (2 Screen Previews)',
    type: 'apk',
    category: 'mobile',
    categoryLabel: 'Mobile App',
    link: '#download-avitrek',
    apkLink: '/assets/apk/AviTrek.apk',
    buttonLabel: 'Download APK',
    image: '/src/assets/images/project_avitrek_1791995266739.jpg',
    galleryImages: [
      '/src/assets/images/project_avitrek_1791995266739.jpg',
      '/src/assets/images/avitrek_map_preview_1791996132206.jpg',
    ],
    description:
      'AviTrek is a birdwatching mobile app that helps users track bird sightings, explore birding hotspots, and record bird data easily with Google Maps integration and Firebase cloud sync.',
    techStack: ['Kotlin', 'Android SDK', 'Google Maps API', 'Firebase Firestore', 'GPS Tracking'],
    highlights: [
      'Interactive GPS hotspot mapping with custom bird sighting markers',
      'Live field guide species identification with high-res media cards',
      'Offline-first sighting caching with Firebase automatic reconciliation',
    ],
  },
  {
    id: 'barberbuzz-platform',
    key: 'buzz',
    title: 'BarberBuzz Platform',
    subtitle: 'Full-Stack Barber Booking Ecosystem & Admin Portal (Mobile & Web)',
    type: 'hybrid',
    category: 'systems',
    categoryLabel: 'Systems & Platform',
    link: 'https://pfanosinyela03.github.io/BarberBuzz_Website_Deployed/',
    apkLink: '/assets/apk/BarberBuzz.apk',
    buttonLabel: 'Visit Website',
    image: '/src/assets/images/project_barberbuzz_enhanced.png',
    galleryImages: [
      '/src/assets/images/project_barberbuzz_enhanced.png',
    ],
    description:
      'BarberBuzz Platform is an integrated appointment management and style discovery system combining a client-facing native Android booking app with a deployed web management administration dashboard.',
    techStack: ['Java', 'Android SDK', 'HTML5 / CSS3', 'JavaScript', 'Firebase Auth', 'Cloud Firestore'],
    highlights: [
      'Interactive visual hairstyle lookbook with style filtering and pricing',
      'Real-time barber chair availability calendar with instant slot confirmation',
      'Web-based administration console for schedule and staff management',
    ],
  },
  {
    id: 'vinyl',
    key: 'vinyl',
    title: 'Vinyl Vault',
    subtitle: 'Vintage Vinyl Cataloging & Valuation Platform',
    type: 'apk',
    category: 'mobile',
    categoryLabel: 'Mobile App',
    link: '#download-vinyl',
    apkLink: '/assets/apk/VinylVault.apk',
    buttonLabel: 'Download APK',
    image: '/src/assets/images/project_vinyl_vault_enhanced.png',
    galleryImages: [
      '/src/assets/images/project_vinyl_vault_enhanced.png',
    ],
    description:
      'Vinyl Vault is a platform for record lovers to catalogue their collection, browse vintage records, track album estimated values, and showcase favorite vinyl finds with rich cover artwork.',
    techStack: ['Android', 'Kotlin', 'Room SQLite', 'Material Design', 'REST API'],
    highlights: [
      'Complete discography tracking with tracklists and sleeve artwork',
      'Estimated portfolio value calculator with real-time analytics',
      'Barcode & catalog search with fast local indexing',
    ],
  },
  {
    id: 'lunarWeb',
    key: 'lunarWeb',
    title: 'Lunar Library',
    subtitle: 'Digital Reading Hub & Book Tracker',
    type: 'web',
    category: 'web',
    categoryLabel: 'Web Platform',
    link: 'https://pfanosinyela03.github.io/LunarLibrary/',
    buttonLabel: 'Visit Website',
    image: '/src/assets/images/project_lunar_library_1791995267351.jpg',
    galleryImages: [
      '/src/assets/images/project_lunar_library_1791995267351.jpg',
    ],
    description:
      'Lunar Library allows users to explore a wide range of books, download or read books online, and keep track of their personal reading history and challenge milestones.',
    techStack: ['Full-Stack Web', 'JavaScript', 'Book APIs', 'Local Storage', 'GitHub Pages'],
    highlights: [
      'Curated virtual shelves for Currently Reading, Want to Read, and Completed',
      'Annual reading challenge goal tracker with progress bars',
      'Clean book discovery interface with reader community reviews',
    ],
  },
  {
    id: 'survey',
    key: 'survey',
    title: 'Tshisima Surveys',
    subtitle: 'Mobile Survey Collection & Real-Time Analytics',
    type: 'apk',
    category: 'mobile',
    categoryLabel: 'Mobile App',
    link: '#download-survey',
    apkLink: '/assets/apk/Survey.apk',
    buttonLabel: 'Download APK',
    image: '/src/assets/images/project_tshisima_surveys.png',
    galleryImages: [
      '/src/assets/images/project_tshisima_surveys.png',
    ],
    description:
      'Tshisima Surveys is an intuitive mobile application for field surveys, opinion polls, and structured questionnaires with instant aggregate analytics and clear visual reporting.',
    techStack: ['Android', 'Kotlin', 'SQLite', 'MPAndroidChart', 'CSV Export'],
    highlights: [
      'Dynamic form builder supporting multiple question types',
      'Offline submission caching with zero data loss in remote areas',
      'One-tap statistical summaries and CSV data export',
    ],
  },
];
