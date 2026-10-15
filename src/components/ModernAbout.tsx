import React from 'react';
import { CheckCircle2, MessageSquare, Sparkles, MapPin, Mail, Award } from 'lucide-react';
import { pfanoProfile } from '../data/pfanoData';

interface ModernAboutProps {
  onContactClick: () => void;
}

export const ModernAbout: React.FC<ModernAboutProps> = ({ onContactClick }) => {
  const highlights = [
    { title: 'Mobile & Android Engineering', desc: 'Crafting responsive Kotlin and Java native Android apps with clean architecture and room databases.' },
    { title: 'Full-Stack Web Systems', desc: 'Designing responsive client applications with React, modern JavaScript, HTML5/CSS3, and robust REST APIs.' },
    { title: 'Enterprise & Database DBA', desc: 'Experience in C# .NET, Oracle DBA administration, SQL schema design, and query optimization.' },
    { title: 'Cloud & CI/CD Tooling', desc: 'Utilizing Azure DevOps, GitHub Actions, and containerized deployment pipelines.' },
  ];

  return (
    <section id="about" className="py-20 border-b border-slate-200/80 bg-slate-50/60">
      <div className="max-w-6xl mx-auto px-6">
        {/* Section Header */}
        <div className="text-center max-w-2xl mx-auto mb-12">
          <div className="text-xs font-bold uppercase tracking-wider text-blue-600 mb-2">
            Get to know me
          </div>
          <h2 className="text-3xl sm:text-4xl font-bold tracking-tight text-slate-900">
            About Me
          </h2>
          <div className="w-16 h-1 bg-blue-600 mx-auto mt-3 rounded-full" />
        </div>

        {/* About Main Card: Smaller Profile Picture Placed Next to Bio */}
        <div className="p-8 sm:p-10 rounded-3xl bg-white border border-slate-200/90 shadow-sm space-y-8">
          <div className="flex flex-col md:flex-row items-center md:items-start gap-8">
            {/* Compact / Smaller Profile Picture (as requested) */}
            <div className="shrink-0 flex flex-col items-center space-y-3">
              <div className="relative w-36 h-36 sm:w-40 sm:h-40 rounded-2xl overflow-hidden bg-slate-100 border-2 border-slate-200/90 shadow-md">
                <img
                  src={pfanoProfile.avatar}
                  alt="Pfano Sinyela Portrait"
                  referrerPolicy="no-referrer"
                  width={1254}
                  height={1254}
                  loading="lazy"
                  decoding="async"
                  draggable={false}
                  className="project-image absolute inset-0 block w-full h-full object-cover object-center"
                />
              </div>
              <div className="text-center">
                <div className="text-xs font-bold text-slate-900">{pfanoProfile.name}</div>
                <div className="text-[11px] font-medium text-blue-600">Full Stack Developer</div>
              </div>
            </div>

            {/* Bio Content Adjacent to the Profile Picture */}
            <div className="flex-1 space-y-4 text-center md:text-left">
              <div>
                <h3 className="text-2xl font-bold text-slate-900 flex items-center justify-center md:justify-start gap-2">
                  <span>{pfanoProfile.aboutSubtitle}</span>
                </h3>
                <div className="text-xs font-semibold text-slate-400 mt-1">
                  Based in South Africa · Available for Global Engagements
                </div>
              </div>

              <p className="text-base text-slate-600 leading-relaxed">
                {pfanoProfile.aboutText}
              </p>

              {/* Special Highlight Box */}
              <div className="p-4 sm:p-5 rounded-2xl bg-blue-50/80 border border-blue-100 flex items-start gap-3.5 text-left">
                <Sparkles className="w-5 h-5 text-blue-600 shrink-0 mt-0.5" />
                <div>
                  <span className="text-xs sm:text-sm font-semibold text-blue-950 block">
                    {pfanoProfile.aboutHighlight}
                  </span>
                  <button
                    onClick={onContactClick}
                    className="mt-2 text-xs font-bold text-blue-700 hover:text-blue-800 underline underline-offset-2 flex items-center gap-1.5 cursor-pointer"
                  >
                    <MessageSquare className="w-3.5 h-3.5" />
                    <span>Send a message directly</span>
                  </button>
                </div>
              </div>
            </div>
          </div>

          {/* 4 Competency Cards Below */}
          <div className="pt-6 border-t border-slate-100 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            {highlights.map((item, idx) => (
              <div
                key={idx}
                className="p-4 rounded-2xl bg-slate-50 border border-slate-200/80 space-y-1.5"
              >
                <div className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />
                  <h4 className="text-xs font-bold text-slate-900">{item.title}</h4>
                </div>
                <p className="text-xs text-slate-500 leading-relaxed">
                  {item.desc}
                </p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  );
};
