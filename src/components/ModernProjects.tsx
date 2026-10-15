import React, { useState } from 'react';
import {
  ArrowUpRight,
  CheckCircle2,
  Download,
  Eye,
  Globe,
  Layers,
  Smartphone,
  Sparkles,
} from 'lucide-react';
import { ProjectItem } from '../data/pfanoData';

interface ModernProjectsProps {
  projects: ProjectItem[];
  onSelectProject: (project: ProjectItem) => void;
  onDownloadApk: (project: ProjectItem) => void;
}

type ProjectFilter = 'all' | 'systems' | 'mobile' | 'web';

export const ModernProjects: React.FC<ModernProjectsProps> = ({
  projects,
  onSelectProject,
  onDownloadApk,
}) => {
  const [filter, setFilter] = useState<ProjectFilter>('all');

  const filtered = filter === 'all'
    ? projects
    : projects.filter((project) => project.category === filter);

  const filters: Array<{ value: ProjectFilter; label: string }> = [
    { value: 'all', label: 'All work' },
    { value: 'systems', label: 'Systems' },
    { value: 'mobile', label: 'Mobile' },
    { value: 'web', label: 'Web' },
  ];

  const projectCount = (value: ProjectFilter) => (
    value === 'all' ? projects.length : projects.filter((project) => project.category === value).length
  );

  return (
    <section id="work" className="relative overflow-hidden py-24 border-b border-slate-200/80 bg-[#f6f8fc]">
      <div className="absolute inset-0 pointer-events-none opacity-60 [background-image:radial-gradient(circle_at_1px_1px,rgba(37,99,235,0.10)_1px,transparent_0)] [background-size:28px_28px]" />
      <div className="absolute -top-40 -right-32 w-[440px] h-[440px] rounded-full bg-blue-200/30 blur-3xl pointer-events-none" />
      <div className="absolute top-1/2 -left-48 w-[380px] h-[380px] rounded-full bg-purple-200/25 blur-3xl pointer-events-none" />

      <div className="relative max-w-7xl mx-auto px-6">
        <div className="flex flex-col lg:flex-row lg:items-end justify-between gap-8 mb-14">
          <div className="max-w-2xl">
            <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-blue-50 border border-blue-100 text-xs font-bold uppercase tracking-wider text-blue-700 mb-4">
              <Sparkles className="w-3.5 h-3.5" />
              Selected work
            </div>
            <h2 className="text-4xl sm:text-5xl font-bold tracking-tight text-slate-950">
              Projects I’ve built.
            </h2>
            <p className="mt-4 text-sm sm:text-base text-slate-600 leading-relaxed max-w-xl">
              A selection of my mobile apps and web platforms.
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-1.5 p-1.5 bg-white/90 backdrop-blur-md border border-slate-200 rounded-2xl shadow-sm self-start lg:self-auto">
            {filters.map((item) => {
              const active = filter === item.value;

              return (
                <button
                  key={item.value}
                  onClick={() => setFilter(item.value)}
                  aria-pressed={active}
                  className={`px-3.5 py-2.5 text-xs font-bold rounded-xl transition-all duration-300 cursor-pointer whitespace-nowrap ${
                    active
                      ? 'bg-slate-950 text-white shadow-md'
                      : 'text-slate-500 hover:text-slate-950 hover:bg-slate-100'
                  }`}
                >
                  {item.label}
                  <span className={`ml-1.5 text-[10px] ${active ? 'text-white/60' : 'text-slate-400'}`}>
                    {projectCount(item.value)}
                  </span>
                </button>
              );
            })}
          </div>
        </div>

        <div className="space-y-10 lg:space-y-14">
          {filtered.map((project, index) => {
            const hasDualPreview = project.id === 'avitrek' && project.galleryImages.length >= 2;
            const preserveFullImage = project.id === 'barberbuzz-platform' || project.id === 'lunarWeb';
            const mediaOnRight = index % 2 === 1;
            const number = String(index + 1).padStart(2, '0');
            const theme = project.category === 'systems'
              ? {
                  panel: 'from-blue-50 via-white to-white',
                  badge: 'bg-blue-100 text-blue-800 border-blue-200',
                  icon: <Layers className="w-4 h-4" />,
                  glow: 'bg-blue-500/20',
                }
              : project.category === 'web'
                ? {
                    panel: 'from-emerald-50 via-white to-white',
                    badge: 'bg-emerald-100 text-emerald-800 border-emerald-200',
                    icon: <Globe className="w-4 h-4" />,
                    glow: 'bg-emerald-500/20',
                  }
                : {
                    panel: 'from-purple-50 via-white to-white',
                    badge: 'bg-purple-100 text-purple-800 border-purple-200',
                    icon: <Smartphone className="w-4 h-4" />,
                    glow: 'bg-purple-500/20',
                  };

            return (
              <article
                key={project.id}
                className="project-showcase-card group relative overflow-hidden rounded-[2rem] bg-white border border-slate-200/90 shadow-[0_18px_70px_-38px_rgba(15,23,42,0.45)] hover:shadow-[0_28px_90px_-42px_rgba(37,99,235,0.45)] hover:border-blue-200 transition-[border-color,box-shadow] duration-500"
                style={{ animationDelay: `${index * 90}ms` }}
              >
                <div className={`absolute -top-24 ${mediaOnRight ? '-left-24' : '-right-24'} w-72 h-72 rounded-full ${theme.glow} blur-3xl pointer-events-none`} />

                <div className="grid grid-cols-1 lg:grid-cols-12 min-h-[520px]">
                  <button
                    type="button"
                    onClick={() => onSelectProject(project)}
                    aria-label={`View ${project.title}`}
                    className={`relative min-h-[320px] sm:min-h-[420px] lg:min-h-full lg:col-span-7 overflow-hidden bg-slate-100 cursor-pointer text-left ${
                      mediaOnRight ? 'lg:order-2' : 'lg:order-1'
                    }`}
                  >
                    {hasDualPreview ? (
                      <div className="absolute inset-0 grid grid-cols-2 gap-1.5 p-1.5 bg-slate-200">
                        {project.galleryImages.slice(0, 2).map((image, imageIndex) => (
                          <div key={image} className="relative overflow-hidden rounded-[1.55rem] bg-white">
                            <img
                              src={image}
                              alt={`${project.title} ${imageIndex === 0 ? 'guide' : 'map'} preview`}
                              referrerPolicy="no-referrer"
                              loading="lazy"
                              decoding="async"
                              draggable={false}
                              className="project-image absolute inset-0 w-full h-full object-cover object-center"
                            />
                            <span className="absolute bottom-4 left-4 px-3 py-1.5 rounded-full bg-slate-950/75 backdrop-blur-md text-[10px] font-bold text-white shadow-lg">
                              {imageIndex === 0 ? 'Field guide' : 'GPS hotspots'}
                            </span>
                          </div>
                        ))}
                      </div>
                    ) : preserveFullImage ? (
                      <img
                        src={project.image}
                        alt={project.title}
                        referrerPolicy="no-referrer"
                        loading="lazy"
                        decoding="async"
                        draggable={false}
                        className="project-image absolute inset-0 w-full h-full object-fill"
                      />
                    ) : (
                      <img
                        src={project.image}
                        alt={project.title}
                        referrerPolicy="no-referrer"
                        loading="lazy"
                        decoding="async"
                        draggable={false}
                        className="project-image absolute inset-0 w-full h-full object-cover object-center"
                      />
                    )}

                    <div className="absolute inset-0 bg-gradient-to-t from-slate-950/55 via-transparent to-slate-950/10 opacity-70 group-hover:opacity-90 transition-opacity duration-500" />
                    <div className="absolute top-5 left-5 flex items-center gap-2">
                      <span className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full border backdrop-blur-md text-[11px] font-bold ${theme.badge}`}>
                        {theme.icon}
                        {project.categoryLabel}
                      </span>
                    </div>
                    <div className="absolute top-5 right-5 w-11 h-11 rounded-full bg-white/90 backdrop-blur-md border border-white/70 shadow-lg flex items-center justify-center text-xs font-mono font-bold text-slate-800">
                      {number}
                    </div>
                    <div className="absolute bottom-5 right-5 inline-flex items-center gap-2 px-4 py-2.5 rounded-full bg-white text-xs font-bold text-slate-950 shadow-xl translate-y-2 opacity-0 group-hover:translate-y-0 group-hover:opacity-100 transition-all duration-300">
                      <Eye className="w-4 h-4 text-blue-600" />
                      View project
                    </div>
                  </button>

                  <div className={`relative lg:col-span-5 flex flex-col justify-between p-7 sm:p-9 lg:p-10 bg-gradient-to-br ${theme.panel} ${
                    mediaOnRight ? 'lg:order-1' : 'lg:order-2'
                  }`}>
                    <div>
                      <div className="flex items-center justify-between gap-4 mb-7">
                        <span className="text-[11px] uppercase tracking-[0.24em] font-bold text-slate-400">
                          Featured build
                        </span>
                        <span className="text-xs font-mono text-slate-400">{number} / {String(filtered.length).padStart(2, '0')}</span>
                      </div>

                      <button
                        type="button"
                        onClick={() => onSelectProject(project)}
                        className="group/title text-left cursor-pointer"
                      >
                        <h3 className="text-3xl lg:text-4xl font-bold tracking-tight text-slate-950 group-hover/title:text-blue-600 transition-colors">
                          {project.title}
                        </h3>
                        <div className="mt-2 flex items-center gap-2 text-sm font-medium text-slate-500">
                          <span>{project.subtitle}</span>
                          <ArrowUpRight className="w-4 h-4 shrink-0 group-hover/title:translate-x-0.5 group-hover/title:-translate-y-0.5 transition-transform" />
                        </div>
                      </button>

                      <p className="mt-6 text-sm text-slate-600 leading-7">
                        {project.description}
                      </p>

                      <div className="mt-7 space-y-3">
                        {project.highlights.slice(0, 3).map((highlight) => (
                          <div key={highlight} className="flex items-start gap-2.5 text-xs text-slate-700 leading-relaxed">
                            <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0 mt-0.5" />
                            <span>{highlight}</span>
                          </div>
                        ))}
                      </div>
                    </div>

                    <div className="mt-9 pt-6 border-t border-slate-200/80">
                      <div className="flex flex-wrap gap-2 mb-5">
                        {project.techStack.slice(0, 4).map((tech) => (
                          <span key={tech} className="px-3 py-1.5 rounded-full bg-white border border-slate-200 text-[10px] font-bold text-slate-600 shadow-2xs">
                            {tech}
                          </span>
                        ))}
                      </div>

                      <div className="flex flex-col sm:flex-row gap-2.5">
                        <button
                          type="button"
                          onClick={() => onSelectProject(project)}
                          className="flex-1 inline-flex items-center justify-center gap-2 px-4 py-3 rounded-xl bg-slate-950 hover:bg-blue-600 text-xs font-bold text-white shadow-md hover:shadow-lg transition-all duration-300 cursor-pointer"
                        >
                          <Eye className="w-4 h-4" />
                          View details
                        </button>

                        {project.type === 'web' ? (
                          <a
                            href={project.link}
                            target="_blank"
                            rel="noreferrer"
                            className="flex-1 inline-flex items-center justify-center gap-2 px-4 py-3 rounded-xl bg-white hover:bg-blue-50 border border-slate-200 hover:border-blue-300 text-xs font-bold text-slate-800 hover:text-blue-700 transition-all duration-300"
                          >
                            <Globe className="w-4 h-4" />
                            Visit website
                          </a>
                        ) : (
                          <button
                            type="button"
                            onClick={() => onDownloadApk(project)}
                            className="flex-1 inline-flex items-center justify-center gap-2 px-4 py-3 rounded-xl bg-white hover:bg-blue-50 border border-slate-200 hover:border-blue-300 text-xs font-bold text-slate-800 hover:text-blue-700 transition-all duration-300 cursor-pointer"
                          >
                            <Download className="w-4 h-4" />
                            Download APK
                          </button>
                        )}
                      </div>

                      {project.type === 'hybrid' && (
                        <a
                          href={project.link}
                          target="_blank"
                          rel="noreferrer"
                          className="mt-2.5 w-full inline-flex items-center justify-center gap-2 px-4 py-3 rounded-xl bg-blue-50 hover:bg-blue-100 border border-blue-100 text-xs font-bold text-blue-700 transition-colors"
                        >
                          <Globe className="w-4 h-4" />
                          Open live platform
                          <ArrowUpRight className="w-3.5 h-3.5" />
                        </a>
                      )}
                    </div>
                  </div>
                </div>
              </article>
            );
          })}
        </div>
      </div>
    </section>
  );
};
