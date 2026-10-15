import React from 'react';
import { Briefcase, Calendar, MapPin, CheckCircle2 } from 'lucide-react';
import { initialExperiences } from '../data/portfolioData';

export const ExperienceTimeline: React.FC = () => {
  return (
    <section id="experience" className="py-20 border-b border-slate-800/60 bg-slate-950/60">
      <div className="max-w-7xl mx-auto px-6">
        {/* Section Header */}
        <div className="max-w-3xl mb-14">
          <div className="text-xs font-semibold uppercase tracking-wider text-blue-400 mb-2">
            Work History & Trajectory
          </div>
          <h2 className="text-2xl sm:text-3xl lg:text-4xl font-bold tracking-tight text-white text-balance">
            Professional Experience & Track Record
          </h2>
          <p className="mt-3 text-base text-slate-300 leading-relaxed">
            Leading engineering initiatives from architecture inception to high-volume production operations.
          </p>
        </div>

        {/* Timeline Stack */}
        <div className="space-y-8">
          {initialExperiences.map((item, idx) => (
            <div
              key={item.id}
              className="relative p-6 sm:p-8 rounded-2xl border border-slate-800/80 bg-slate-900/50 hover:border-slate-700 transition-all space-y-6"
            >
              <div className="flex flex-col md:flex-row md:items-start justify-between gap-4">
                <div>
                  <div className="text-xs font-medium text-blue-400 mb-1">
                    {item.company} · {item.type}
                  </div>
                  <h3 className="text-xl font-bold text-white">
                    {item.role}
                  </h3>
                </div>

                <div className="flex flex-wrap items-center gap-3 text-xs text-slate-400">
                  <div className="flex items-center gap-1.5 bg-slate-950/60 px-3 py-1.5 rounded-md border border-slate-800">
                    <Calendar className="w-3.5 h-3.5 text-blue-400" />
                    <span>{item.period}</span>
                  </div>
                  <div className="flex items-center gap-1.5 bg-slate-950/60 px-3 py-1.5 rounded-md border border-slate-800">
                    <MapPin className="w-3.5 h-3.5 text-slate-400" />
                    <span>{item.location}</span>
                  </div>
                </div>
              </div>

              <p className="text-sm text-slate-300 leading-relaxed">
                {item.description}
              </p>

              {/* Quantified Accomplishments */}
              <div className="space-y-2 pt-2">
                <div className="text-xs font-semibold uppercase tracking-wider text-slate-400">
                  Key Accomplishments & Impact:
                </div>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                  {item.highlights.map((highlight, hIdx) => (
                    <div
                      key={hIdx}
                      className="p-3 rounded-lg bg-slate-950/50 border border-slate-800/80 text-xs text-slate-300 leading-relaxed flex items-start gap-2"
                    >
                      <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
                      <span>{highlight}</span>
                    </div>
                  ))}
                </div>
              </div>

              {/* Technologies unboxed */}
              <div className="pt-2 flex flex-wrap items-center gap-2 text-xs">
                <span className="text-slate-500 font-medium">Stack:</span>
                {item.skills.map((skill, sIdx) => (
                  <span
                    key={sIdx}
                    className="text-slate-400 bg-slate-950 px-2.5 py-1 rounded border border-slate-800 font-mono text-[11px]"
                  >
                    {skill}
                  </span>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};
