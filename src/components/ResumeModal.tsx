import React from 'react';
import {
  X,
  Download,
  FileText,
  Mail,
  MapPin,
  Phone,
  Github,
  CheckCircle,
} from 'lucide-react';
import { pfanoProfile } from '../data/pfanoData';

interface ResumeModalProps {
  isOpen: boolean;
  onClose: () => void;
  onDownloadResume: () => void;
}

const DetailList: React.FC<{ items: string[] }> = ({ items }) => (
  <ul className="mt-2 space-y-1.5 text-slate-700">
    {items.map((item) => (
      <li key={item} className="flex items-start gap-2 leading-relaxed">
        <CheckCircle className="w-3.5 h-3.5 text-emerald-600 shrink-0 mt-0.5" />
        <span>{item}</span>
      </li>
    ))}
  </ul>
);

export const ResumeModal: React.FC<ResumeModalProps> = ({
  isOpen,
  onClose,
  onDownloadResume,
}) => {
  if (!isOpen) return null;

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6 bg-slate-900/50 backdrop-blur-xs overflow-y-auto"
      onClick={onClose}
    >
      <div
        className="relative w-full max-w-4xl bg-white border border-slate-200 rounded-3xl shadow-2xl overflow-hidden my-auto animate-in zoom-in-95 duration-200"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100 bg-slate-50/80">
          <div className="flex items-center gap-2">
            <div className="p-1.5 rounded-lg bg-blue-50 text-blue-600">
              <FileText className="w-4 h-4" />
            </div>
            <div>
              <h3 className="text-base font-bold text-slate-900">Curriculum Vitae</h3>
              <div className="text-[11px] text-slate-500">{pfanoProfile.name} - Software Engineer</div>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 text-slate-400 hover:text-slate-700 hover:bg-slate-200/60 rounded-full transition-colors cursor-pointer"
            aria-label="Close CV"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="p-6 max-h-[72vh] overflow-y-auto space-y-6 text-slate-800 text-xs">
          <div className="p-5 rounded-2xl bg-slate-50 border border-slate-200 space-y-2">
            <h2 className="text-xl font-bold text-slate-900">{pfanoProfile.name}</h2>
            <div className="text-sm font-semibold text-blue-600">
              Software Engineer - Oracle Database Administrator - Full-Stack Developer
            </div>
            <div className="flex flex-wrap items-center gap-x-4 gap-y-2 text-slate-500 pt-1">
              <span className="flex items-center gap-1.5">
                <Phone className="w-3.5 h-3.5 text-slate-400" />
                <span>063 604 0891</span>
              </span>
              <span className="flex items-center gap-1.5">
                <Mail className="w-3.5 h-3.5 text-slate-400" />
                <span>{pfanoProfile.email}</span>
              </span>
              <span className="flex items-center gap-1.5">
                <MapPin className="w-3.5 h-3.5 text-slate-400" />
                <span>Johannesburg, South Africa</span>
              </span>
              <a
                href={pfanoProfile.github}
                target="_blank"
                rel="noreferrer"
                className="flex items-center gap-1.5 hover:text-blue-600 transition-colors"
              >
                <Github className="w-3.5 h-3.5 text-slate-400" />
                <span>github.com/pfanosinyela03</span>
              </a>
            </div>
          </div>

          <section className="space-y-2">
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500 border-b border-slate-200 pb-1">
              Professional Profile
            </h4>
            <p className="text-slate-700 leading-relaxed">
              Diploma in IT Software Development graduate from Rosebank College with a strong foundation in programming, database systems, and software engineering. Skilled in Java, C#, PHP, Kotlin, SQL, HTML/CSS, Flutter, Firebase, and backend development, with experience in Oracle database administration and software engineering.
            </p>
          </section>

          <section className="space-y-2">
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500 border-b border-slate-200 pb-1">
              Technical Skills
            </h4>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-slate-700">
              <div className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                <strong>Languages:</strong> Java, C#, Kotlin, JavaScript, SQL, PHP, HTML/CSS, Flutter & Dart
              </div>
              <div className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                <strong>Databases & Systems:</strong> Oracle Database, MySQL, Firebase, database administration, performance monitoring
              </div>
              <div className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                <strong>Tools & Platforms:</strong> Visual Studio, VS Code, NetBeans, Android Studio, FlutterFlow, Azure DevOps
              </div>
              <div className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                <strong>Concepts:</strong> Full-stack development, application development, database management, Git version control
              </div>
            </div>
          </section>

          <section className="space-y-3">
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500 border-b border-slate-200 pb-1">
              Work Experience
            </h4>
            <div className="p-4 rounded-xl bg-slate-50 border border-slate-100">
              <div className="flex flex-col sm:flex-row sm:justify-between gap-1 font-bold text-slate-900">
                <span>Software Engineer | Aegar Tech</span>
                <span className="text-emerald-700 font-mono text-[11px]">June 2026 - Present</span>
              </div>
              <DetailList items={[
                'Contribute to the development, maintenance, and improvement of software applications.',
                'Integrate third-party services and APIs, including communication and call-handling features.',
                'Investigate and resolve application bugs through testing, debugging, and code reviews.',
                'Use Azure DevOps and GitHub for version control, code management, and team collaboration.',
              ]} />
            </div>

            <div className="p-4 rounded-xl bg-slate-50 border border-slate-100">
              <div className="flex flex-col sm:flex-row sm:justify-between gap-1 font-bold text-slate-900">
                <span>Database Administrator Intern | OptiSolutions</span>
                <span className="text-slate-600 font-mono text-[11px]">Sep 2025 - May 2026</span>
              </div>
              <DetailList items={[
                'Performed database patching and supported routine health checks.',
                'Managed database accounts, password resets, account unlocking, and inactive accounts.',
                'Supported database link updates and connectivity between database systems.',
                'Tracked database requests and completed tasks through ticketing and support workflows.',
              ]} />
            </div>
          </section>

          <section className="space-y-3">
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500 border-b border-slate-200 pb-1">
              Education
            </h4>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                <div className="font-bold text-slate-900">Rosebank College Braamfontein</div>
                <div className="text-blue-600 font-semibold text-[11px]">Diploma in IT Software Development</div>
                <div className="text-slate-500 font-mono text-[11px] mt-1">2021 - 2024</div>
              </div>
              <div className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                <div className="font-bold text-slate-900">Makhado Comprehensive Secondary School</div>
                <div className="text-blue-600 font-semibold text-[11px]">National Senior Certificate (Grade 12)</div>
                <div className="text-slate-500 font-mono text-[11px] mt-1">2016 - 2020</div>
              </div>
            </div>
          </section>

          <section className="space-y-2">
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500 border-b border-slate-200 pb-1">
              Selected Projects
            </h4>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
              <div className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                <strong>BarberBuzz:</strong> Barber shop application and website using Firebase Realtime Database, Azure DevOps, and GitHub collaboration.
              </div>
              <div className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                <strong>Vinyl Vault:</strong> Application developed for vinyl collection management.
              </div>
              <div className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                <strong>AviTrek:</strong> Birdwatching hotspot application with location-focused functionality.
              </div>
              <div className="p-3 rounded-xl bg-slate-50 border border-slate-100">
                <strong>Lunar Library:</strong> Website where users can upload and share PDF books.
              </div>
            </div>
          </section>

          <section className="space-y-2">
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500 border-b border-slate-200 pb-1">
              Reference
            </h4>
            <div className="p-3 rounded-xl bg-slate-50 border border-slate-100 text-slate-700">
              <strong>Lesley Muller</strong> - HR, OptiSolutions · lcmuller@optisolutions.co.za · 072 842 1126
            </div>
          </section>
        </div>

        <div className="flex items-center justify-between px-6 py-4 border-t border-slate-100 bg-slate-50/80">
          <button
            onClick={onClose}
            className="px-4 py-2 text-xs font-semibold text-slate-600 hover:text-slate-900 transition-colors cursor-pointer"
          >
            Close
          </button>
          <button
            onClick={onDownloadResume}
            className="flex items-center gap-2 py-3 px-5 text-xs font-bold text-white bg-blue-600 hover:bg-blue-700 active:scale-98 rounded-2xl shadow-sm hover:shadow-md transition-all cursor-pointer"
          >
            <Download className="w-4 h-4" />
            <span>Download CV PDF</span>
          </button>
        </div>
      </div>
    </div>
  );
};
