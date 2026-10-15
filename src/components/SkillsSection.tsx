import React from 'react';
import { Cpu, Server, Compass, CheckCircle } from 'lucide-react';
import { initialSkills } from '../data/portfolioData';

export const SkillsSection: React.FC = () => {
  const icons = [Cpu, Server, Compass];

  return (
    <section id="skills" className="py-20 border-b border-slate-800/60 bg-slate-950/20">
      <div className="max-w-7xl mx-auto px-6">
        {/* Section Header */}
        <div className="max-w-3xl mb-14">
          <div className="text-xs font-semibold uppercase tracking-wider text-blue-400 mb-2">
            Technical Stack & Invariants
          </div>
          <h2 className="text-2xl sm:text-3xl lg:text-4xl font-bold tracking-tight text-white text-balance">
            Engineering Competencies & Systems Depth
          </h2>
          <p className="mt-3 text-base text-slate-300 leading-relaxed">
            Strict adherence to type safety, resilient distributed architecture, and human-centered frontend usability.
          </p>
        </div>

        {/* 3-Column Skills Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {initialSkills.map((category, catIdx) => {
            const Icon = icons[catIdx % icons.length];
            return (
              <div
                key={category.title}
                className="flex flex-col justify-between p-6 rounded-2xl border border-slate-800/80 bg-slate-900/40 hover:border-slate-700 transition-all space-y-6"
              >
                <div>
                  <div className="flex items-center gap-3 mb-3">
                    <div className="p-2.5 rounded-lg bg-blue-600/10 border border-blue-500/20 text-blue-400">
                      <Icon className="w-5 h-5" />
                    </div>
                    <div>
                      <h3 className="text-lg font-bold text-white">{category.title}</h3>
                    </div>
                  </div>
                  <p className="text-xs text-slate-400 leading-relaxed">
                    {category.description}
                  </p>

                  {/* Skills List with Proficiency & Context */}
                  <div className="mt-6 space-y-5">
                    {category.skills.map((skill) => (
                      <div key={skill.name} className="space-y-1.5">
                        <div className="flex items-center justify-between text-xs">
                          <span className="font-semibold text-slate-200">{skill.name}</span>
                          <span className="font-mono text-slate-400 tabular-nums">{skill.level}%</span>
                        </div>
                        {/* Progress Bar */}
                        <div className="h-1.5 w-full bg-slate-800 rounded-full overflow-hidden">
                          <div
                            className="h-full bg-blue-500 rounded-full transition-all duration-1000"
                            style={{ width: `${skill.level}%` }}
                          />
                        </div>
                        <div className="text-[11px] text-slate-400">
                          {skill.note}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>

                <div className="pt-4 border-t border-slate-800/80 flex items-center gap-2 text-xs text-slate-400">
                  <CheckCircle className="w-3.5 h-3.5 text-blue-400 shrink-0" />
                  <span>Production verified across client deployments</span>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
};
