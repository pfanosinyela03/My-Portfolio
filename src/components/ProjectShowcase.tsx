import React, { useState } from 'react';
import { ArrowUpRight, Layers, ExternalLink, Sparkles } from 'lucide-react';
import { Project } from '../data/portfolioData';

interface ProjectShowcaseProps {
  projects: Project[];
  onSelectProject: (project: Project) => void;
}

export const ProjectShowcase: React.FC<ProjectShowcaseProps> = ({ projects, onSelectProject }) => {
  const [selectedFilter, setSelectedFilter] = useState<string>('All Works');

  const categories = ['All Works', 'Full-Stack', 'Cloud & Distributed', 'Mobile & UI'];

  const filteredProjects = selectedFilter === 'All Works'
    ? projects
    : projects.filter(p => p.category === selectedFilter);

  return (
    <section id="works" className="py-20 border-b border-slate-800/60 bg-slate-950/40">
      <div className="max-w-7xl mx-auto px-6">
        {/* Section Header with Functional Segmented Filter Tabs */}
        <div className="flex flex-col md:flex-row md:items-end justify-between gap-6 pb-12 border-b border-slate-800/60">
          <div>
            <div className="text-xs font-semibold uppercase tracking-wider text-blue-400 mb-2">
              Portfolio & Engineering Case Studies
            </div>
            <h2 className="text-2xl sm:text-3xl lg:text-4xl font-bold tracking-tight text-white text-balance">
              Selected Works & Architectures
            </h2>
          </div>

          {/* Functional Filter Tabs (Segmented control) */}
          <div className="flex items-center gap-1 p-1 bg-slate-900 border border-slate-800 rounded-xl overflow-x-auto">
            {categories.map((cat) => {
              const isActive = selectedFilter === cat;
              return (
                <button
                  key={cat}
                  onClick={() => setSelectedFilter(cat)}
                  className={`px-3.5 py-1.5 text-xs font-medium rounded-lg transition-all whitespace-nowrap shrink-0 ${
                    isActive
                      ? 'bg-blue-600 text-white shadow-sm'
                      : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                  }`}
                >
                  {cat}
                </button>
              );
            })}
          </div>
        </div>

        {/* Dynamic Bento Grid of Projects */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-12 gap-8 pt-12">
          {filteredProjects.map((project, idx) => {
            const isFeaturedWide = idx === 0 || (idx === 1 && filteredProjects.length === 2);
            const colSpan = isFeaturedWide ? 'lg:col-span-8' : 'lg:col-span-4';

            return (
              <div
                key={project.id}
                onClick={() => onSelectProject(project)}
                className={`${colSpan} group cursor-pointer relative flex flex-col justify-between rounded-2xl border border-slate-800/80 bg-slate-900/60 hover:border-slate-700 transition-all duration-300 overflow-hidden hover:shadow-xl hover:shadow-blue-500/5`}
              >
                {/* Media Container with resilient zero-broken image fallback */}
                <div className="relative aspect-[16/10] w-full overflow-hidden bg-slate-950">
                  <img
                    src={project.image}
                    alt={project.title}
                    referrerPolicy="no-referrer"
                    onError={(e) => {
                      // Fallback gracefully to styled container
                      (e.target as HTMLElement).style.display = 'none';
                    }}
                    className="w-full h-full object-cover object-center group-hover:scale-103 transition-transform duration-500"
                  />
                  {/* Scrim Overlay */}
                  <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-950/20 to-transparent" />

                  {/* Impact Highlight Badge (Clean unboxed text top-right) */}
                  <div className="absolute top-4 right-4 bg-slate-950/80 backdrop-blur-md border border-slate-700/60 px-3 py-1 rounded-md text-[11px] font-mono font-medium text-blue-300">
                    {project.impact}
                  </div>
                </div>

                {/* Content Area */}
                <div className="p-6 flex-1 flex flex-col justify-between space-y-4">
                  <div>
                    {/* Unboxed Metadata (Zero-pill discipline) */}
                    <div className="flex items-center gap-2 text-xs font-medium text-slate-400 mb-2">
                      <span className="text-blue-400">{project.category}</span>
                      <span aria-hidden="true">·</span>
                      <span>{project.year}</span>
                      <span aria-hidden="true">·</span>
                      <span>{project.client}</span>
                    </div>

                    <h3 className="text-xl font-bold text-white group-hover:text-blue-400 transition-colors flex items-center justify-between">
                      <span>{project.title}</span>
                      <ArrowUpRight className="w-4 h-4 text-slate-500 group-hover:text-blue-400 group-hover:translate-x-0.5 group-hover:-translate-y-0.5 transition-all" />
                    </h3>

                    <p className="mt-2 text-sm text-slate-300 leading-relaxed">
                      {project.summary}
                    </p>
                  </div>

                  {/* Key Tech stack unboxed text */}
                  <div className="pt-4 border-t border-slate-800/80 flex items-center justify-between text-xs text-slate-400">
                    <div className="flex flex-wrap gap-2">
                      {project.technologies.slice(0, 4).map((tech, i) => (
                        <span key={i} className="text-slate-400">
                          {tech}{i < Math.min(project.technologies.length, 4) - 1 ? ' ·' : ''}
                        </span>
                      ))}
                    </div>
                    <span className="text-blue-400 font-medium group-hover:underline">Case Study</span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
};
