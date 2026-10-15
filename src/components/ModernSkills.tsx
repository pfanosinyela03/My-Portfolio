import React from 'react';
import { Smartphone, Code, Cpu, Globe, Cloud, Database, Check } from 'lucide-react';
import { skillsList } from '../data/pfanoData';

export const ModernSkills: React.FC = () => {
  const getIcon = (name: string) => {
    switch (name) {
      case 'Kotlin':
        return <Smartphone className="w-5 h-5 text-purple-600" />;
      case 'Flutter':
        return <Smartphone className="w-5 h-5 text-sky-600" />;
      case 'Java':
        return <Cpu className="w-5 h-5 text-amber-600" />;
      case 'JavaScript':
        return <Code className="w-5 h-5 text-yellow-600" />;
      case 'C#':
        return <Code className="w-5 h-5 text-emerald-600" />;
      case 'Web Development':
        return <Globe className="w-5 h-5 text-blue-600" />;
      case 'Azure DevOps':
        return <Cloud className="w-5 h-5 text-sky-600" />;
      case 'Firebase':
        return <Cloud className="w-5 h-5 text-orange-500" />;
      case 'SQL & Oracle DBA':
        return <Database className="w-5 h-5 text-rose-600" />;
      default:
        return <Code className="w-5 h-5 text-blue-600" />;
    }
  };

  return (
    <section id="skills" className="py-20 border-b border-slate-200/80 bg-white">
      <div className="max-w-6xl mx-auto px-6">
        {/* Section Header */}
        <div className="text-center max-w-2xl mx-auto mb-14">
          <div className="text-xs font-bold uppercase tracking-wider text-blue-600 mb-2">
            Technical Arsenal
          </div>
          <h2 className="text-3xl sm:text-4xl font-bold tracking-tight text-slate-900">
            My Skills
          </h2>
          <div className="w-16 h-1 bg-blue-600 mx-auto mt-3 rounded-full" />
          <p className="mt-4 text-base text-slate-600 leading-relaxed">
            I am experienced in a range of modern development tools and languages across mobile, backend, frontend, and cloud platforms.
          </p>
        </div>

        {/* Modern Skills Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {skillsList.map((skill) => (
            <div
              key={skill.name}
              className="p-6 rounded-2xl bg-white border border-slate-200/80 hover:border-blue-300 hover:shadow-md transition-all duration-300 space-y-4 group"
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-100 group-hover:bg-blue-50/50 transition-colors">
                    {getIcon(skill.name)}
                  </div>
                  <div>
                    <h3 className="text-base font-bold text-slate-900 group-hover:text-blue-600 transition-colors">
                      {skill.name}
                    </h3>
                    <div className="text-xs text-slate-400 font-medium">
                      {skill.category}
                    </div>
                  </div>
                </div>
                <span className="text-xs font-mono font-bold text-slate-600">
                  {skill.level}%
                </span>
              </div>

              {/* Progress Bar */}
              <div className="w-full h-2 bg-slate-100 rounded-full overflow-hidden">
                <div
                  className="h-full bg-blue-600 rounded-full transition-all duration-1000"
                  style={{ width: `${skill.level}%` }}
                />
              </div>

              <p className="text-xs text-slate-500 leading-relaxed">
                {skill.description}
              </p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};
