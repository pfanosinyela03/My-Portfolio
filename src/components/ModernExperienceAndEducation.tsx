import React from 'react';
import { Briefcase, GraduationCap, MapPin, Calendar, CheckCircle2, Diamond, Building2 } from 'lucide-react';
import { experiencesList, educationList } from '../data/pfanoData';

export const ModernExperienceAndEducation: React.FC = () => {
  return (
    <section id="experience" className="py-20 border-b border-slate-200/80 bg-white">
      <div className="max-w-6xl mx-auto px-6">
        {/* Section Header */}
        <div className="text-center max-w-2xl mx-auto mb-14">
          <div className="text-xs font-bold uppercase tracking-wider text-blue-600 mb-2">
            Professional Background
          </div>
          <h2 className="text-3xl sm:text-4xl font-bold tracking-tight text-slate-900">
            Experience & Education
          </h2>
          <div className="w-16 h-1 bg-blue-600 mx-auto mt-3 rounded-full" />
        </div>

        {/* 2-Column Grid: Experience & Education */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-10 items-start">
          {/* Experience Column (7 cols) */}
          <div className="lg:col-span-7 space-y-6">
            <div className="flex items-center gap-2.5 pb-2 border-b border-slate-200">
              <div className="p-2 rounded-xl bg-blue-50 text-blue-600">
                <Briefcase className="w-5 h-5" />
              </div>
              <div>
                <h3 className="text-xl font-bold text-slate-900">Professional Experience</h3>
                <div className="text-xs text-slate-500 font-medium">Software Engineering & Database Administration</div>
              </div>
            </div>

            <div className="space-y-5">
              {experiencesList.map((exp) => (
                <div
                  key={exp.id}
                  className="p-6 rounded-3xl bg-slate-50 border border-slate-200/90 hover:border-blue-300 hover:shadow-md transition-all duration-300 space-y-4"
                >
                  <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-3">
                    <div className="flex items-start gap-3">
                      <div className="p-3 rounded-2xl bg-white border border-slate-200 shadow-2xs text-slate-700 shrink-0">
                        <Building2 className="w-5 h-5 text-blue-600" />
                      </div>
                      <div>
                        <div className="flex items-center gap-2">
                          <h4 className="text-lg font-bold text-slate-900">{exp.role}</h4>
                          {exp.isCurrent && (
                            <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-100 text-emerald-800 border border-emerald-200">
                              Present
                            </span>
                          )}
                        </div>
                        <div className="text-xs font-semibold text-blue-600">
                          {exp.company} · {exp.employmentType}
                        </div>
                      </div>
                    </div>

                    <div className="text-left sm:text-right text-xs text-slate-500 space-y-0.5">
                      <div className="font-semibold text-slate-700">{exp.period}</div>
                      {exp.duration && (
                        <div className="text-slate-400 font-mono text-[11px]">{exp.duration}</div>
                      )}
                    </div>
                  </div>

                  <div className="flex items-center gap-1.5 text-xs text-slate-500">
                    <MapPin className="w-3.5 h-3.5 text-slate-400 shrink-0" />
                    <span>{exp.location}</span>
                  </div>

                  <p className="text-xs text-slate-600 leading-relaxed">
                    {exp.description}
                  </p>

                  <div className="pt-2 flex flex-wrap gap-1.5">
                    {exp.skills.map((skill, sIdx) => (
                      <span
                        key={sIdx}
                        className="px-2.5 py-1 rounded-lg bg-white border border-slate-200 text-slate-700 text-[11px] font-medium"
                      >
                        {skill}
                      </span>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Education Column (5 cols) */}
          <div className="lg:col-span-5 space-y-6">
            <div className="flex items-center gap-2.5 pb-2 border-b border-slate-200">
              <div className="p-2 rounded-xl bg-purple-50 text-purple-600">
                <GraduationCap className="w-5 h-5" />
              </div>
              <div>
                <h3 className="text-xl font-bold text-slate-900">Education</h3>
                <div className="text-xs text-slate-500 font-medium">Academic Credentials & Qualifications</div>
              </div>
            </div>

            <div className="space-y-5">
              {educationList.map((edu) => (
                <div
                  key={edu.id}
                  className="p-6 rounded-3xl bg-slate-50 border border-slate-200/90 hover:border-purple-300 hover:shadow-md transition-all duration-300 space-y-4"
                >
                  <div className="flex items-start gap-3.5">
                    <div className="p-3 rounded-2xl bg-pink-100/80 border border-pink-200 text-pink-700 shrink-0 font-bold text-xs">
                      RI
                    </div>
                    <div>
                      <h4 className="text-base font-bold text-slate-900 leading-snug">
                        {edu.institution}
                      </h4>
                      <div className="text-xs font-semibold text-blue-600 mt-1">
                        {edu.qualification}
                      </div>
                      <div className="text-xs text-slate-400 mt-0.5">
                        {edu.period}
                      </div>
                    </div>
                  </div>

                  {/* Specialization Badge */}
                  <div className="p-3.5 rounded-2xl bg-white border border-slate-200 flex items-center gap-2.5 text-xs text-slate-800 font-semibold shadow-2xs">
                    <Diamond className="w-4 h-4 text-purple-600 shrink-0" />
                    <span>Specialization: {edu.specialization}</span>
                  </div>

                  <p className="text-xs text-slate-600 leading-relaxed">
                    {edu.details}
                  </p>
                </div>
              ))}
            </div>

            {/* Quick summary certificate / credibility note */}
            <div className="p-5 rounded-3xl bg-blue-50/70 border border-blue-100 text-xs text-blue-900 space-y-2">
              <div className="font-bold flex items-center gap-1.5">
                <CheckCircle2 className="w-4 h-4 text-blue-600" />
                <span>Verified Technical Qualifications</span>
              </div>
              <p className="text-blue-800 leading-relaxed">
                Diploma completed with focused specialization in Software Development, object-oriented software engineering, and database management.
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
