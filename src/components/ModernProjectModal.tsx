import React, { useEffect, useRef, useState } from 'react';
import { createPortal } from 'react-dom';
import { X, ChevronLeft, ChevronRight, Download, Globe, Smartphone, Check, Layers, Maximize2, Minimize2 } from 'lucide-react';
import { ProjectItem } from '../data/pfanoData';

interface ModernProjectModalProps {
  project: ProjectItem | null;
  onClose: () => void;
  onDownloadApk: (project: ProjectItem) => void;
}

export const ModernProjectModal: React.FC<ModernProjectModalProps> = ({
  project,
  onClose,
  onDownloadApk,
}) => {
  const [currentSlide, setCurrentSlide] = useState(0);
  const [isViewerFullscreen, setIsViewerFullscreen] = useState(false);
  const lastTapAt = useRef(0);

  useEffect(() => {
    setCurrentSlide(0);
    setIsViewerFullscreen(false);
  }, [project]);

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        if (isViewerFullscreen) {
          setIsViewerFullscreen(false);
        } else {
          onClose();
        }
      }
      if (e.key === 'ArrowRight' && project) {
        setCurrentSlide((prev) => (prev + 1) % project.galleryImages.length);
      }
      if (e.key === 'ArrowLeft' && project) {
        setCurrentSlide((prev) => (prev - 1 + project.galleryImages.length) % project.galleryImages.length);
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isViewerFullscreen, project, onClose]);

  useEffect(() => {
    if (!isViewerFullscreen) return;

    const previousOverflow = document.body.style.overflow;
    document.body.style.overflow = 'hidden';

    return () => {
      document.body.style.overflow = previousOverflow;
    };
  }, [isViewerFullscreen]);

  if (!project) return null;

  const nextSlide = () => {
    setCurrentSlide((prev) => (prev + 1) % project.galleryImages.length);
  };

  const prevSlide = () => {
    setCurrentSlide((prev) => (prev - 1 + project.galleryImages.length) % project.galleryImages.length);
  };

  const toggleViewerFullscreen = () => {
    setIsViewerFullscreen((current) => !current);
  };

  const handleViewerTouchEnd = (e: React.TouchEvent<HTMLDivElement>) => {
    if ((e.target as HTMLElement).closest('button')) return;

    const tappedAt = Date.now();
    if (tappedAt - lastTapAt.current < 320) {
      e.preventDefault();
      lastTapAt.current = 0;
      toggleViewerFullscreen();
      return;
    }

    lastTapAt.current = tappedAt;
  };

  return (
    <>
      <div
        className="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6 bg-slate-900/50 backdrop-blur-xs overflow-y-auto"
        onClick={onClose}
      >
        <div
          className="relative w-full max-w-5xl bg-white border border-slate-200 rounded-3xl shadow-2xl overflow-hidden my-auto animate-in zoom-in-95 duration-200"
          onClick={(e) => e.stopPropagation()}
        >
          {/* Header Bar */}
          <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100 bg-slate-50/80">
            <div className="flex items-center gap-2">
              <span className="p-1.5 rounded-lg bg-blue-50 text-blue-600">
                {project.category === 'systems' ? (
                  <Layers className="w-4 h-4 text-blue-600" />
                ) : project.type === 'apk' ? (
                  <Smartphone className="w-4 h-4 text-purple-600" />
                ) : (
                  <Globe className="w-4 h-4 text-emerald-600" />
                )}
              </span>
              <div>
                <div className="flex items-center gap-2">
                  <h3 className="text-base font-bold text-slate-900">{project.title}</h3>
                  <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-blue-100 text-blue-800">
                    {project.categoryLabel}
                  </span>
                </div>
                <div className="text-[11px] text-slate-500 font-medium">{project.subtitle}</div>
              </div>
            </div>
            <button
              onClick={onClose}
              className="p-1.5 text-slate-400 hover:text-slate-700 hover:bg-slate-200/60 rounded-full transition-colors cursor-pointer"
              aria-label="Close modal"
            >
              <X className="w-5 h-5" />
            </button>
          </div>

          {/* Modal Scrollable Content */}
          <div className="p-6 max-h-[75vh] overflow-y-auto space-y-6">
            {/* Slider Container */}
            <div
              className="relative rounded-2xl overflow-hidden bg-white border border-slate-200 shadow-sm group select-none cursor-zoom-in touch-manipulation"
              onDoubleClick={toggleViewerFullscreen}
              onTouchEnd={handleViewerTouchEnd}
              title="Double-click or double-tap to view fullscreen"
            >
              <img
                src={project.galleryImages[currentSlide]}
                alt={`${project.title} Screenshot ${currentSlide + 1}`}
                referrerPolicy="no-referrer"
                decoding="async"
                draggable={false}
                className="project-image block w-full h-auto max-h-[68vh] object-contain"
              />

              <button
                onClick={(e) => {
                  e.stopPropagation();
                  toggleViewerFullscreen();
                }}
                onDoubleClick={(e) => e.stopPropagation()}
                className="absolute top-3 right-3 p-2.5 rounded-full bg-white/95 hover:bg-white text-slate-800 border border-slate-200 shadow-md transition-all cursor-pointer"
                aria-label="View image fullscreen"
                title="View fullscreen"
              >
                <Maximize2 className="w-4 h-4" />
              </button>

              {/* Slider navigation buttons if multiple images */}
              {project.galleryImages.length > 1 && (
                <>
                  <button
                    onClick={prevSlide}
                    onDoubleClick={(e) => e.stopPropagation()}
                    className="absolute left-3 top-1/2 -translate-y-1/2 p-2 rounded-full bg-white/95 hover:bg-white text-slate-800 border border-slate-200 shadow-md transition-all cursor-pointer"
                    aria-label="Previous slide"
                  >
                    <ChevronLeft className="w-5 h-5" />
                  </button>
                  <button
                    onClick={nextSlide}
                    onDoubleClick={(e) => e.stopPropagation()}
                    className="absolute right-3 top-1/2 -translate-y-1/2 p-2 rounded-full bg-white/95 hover:bg-white text-slate-800 border border-slate-200 shadow-md transition-all cursor-pointer"
                    aria-label="Next slide"
                  >
                    <ChevronRight className="w-5 h-5" />
                  </button>

                  {/* Dot indicators */}
                  <div className="absolute bottom-3 left-1/2 -translate-x-1/2 flex items-center gap-1.5 bg-white/95 border border-slate-200 px-3 py-1.5 rounded-full shadow-md">
                    {project.galleryImages.map((_, idx) => (
                      <button
                        key={idx}
                        onClick={() => setCurrentSlide(idx)}
                        className={`h-2 rounded-full transition-all ${
                          idx === currentSlide ? 'bg-blue-600 w-4' : 'bg-slate-300 w-2'
                        }`}
                        aria-label={`View image ${idx + 1}`}
                      />
                    ))}
                  </div>
                </>
              )}
            </div>

          {/* Project Overview */}
          <div className="space-y-3">
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500">
              Project Overview
            </h4>
            <p className="text-sm text-slate-700 leading-relaxed">
              {project.description}
            </p>
          </div>

          {/* Key Feature Highlights */}
          {project.highlights && project.highlights.length > 0 && (
            <div className="space-y-2.5">
              <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500">
                Key Capabilities & Features
              </h4>
              <div className="space-y-2">
                {project.highlights.map((highlight, idx) => (
                  <div key={idx} className="flex items-start gap-2.5 text-xs text-slate-700">
                    <Check className="w-4 h-4 text-emerald-600 shrink-0 mt-0.5" />
                    <span>{highlight}</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Tech Stack Chips */}
          <div className="space-y-2">
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500">
              Technologies Used
            </h4>
            <div className="flex flex-wrap gap-2">
              {project.techStack.map((tech, idx) => (
                <span
                  key={idx}
                  className="px-2.5 py-1 rounded-lg bg-slate-100 text-slate-700 text-xs font-medium border border-slate-200"
                >
                  {tech}
                </span>
              ))}
            </div>
          </div>
          </div>

          {/* Modal Bottom Action Footer */}
          <div className="flex items-center justify-between px-6 py-4 border-t border-slate-100 bg-slate-50/80">
            <button
              onClick={onClose}
              className="px-4 py-2 text-xs font-semibold text-slate-600 hover:text-slate-900 transition-colors cursor-pointer"
            >
              Close
            </button>

            <div className="flex items-center gap-2">
              {project.type === 'hybrid' ? (
                <>
                  <button
                    onClick={() => onDownloadApk(project)}
                    className="flex items-center gap-2 py-2.5 px-4 text-xs font-bold text-white bg-slate-900 hover:bg-blue-600 rounded-xl shadow-sm active:scale-98 transition-all cursor-pointer"
                  >
                    <Download className="w-4 h-4" />
                    <span>Download APK</span>
                  </button>
                  <a
                    href={project.link}
                    target="_blank"
                    rel="noreferrer"
                    className="flex items-center gap-2 py-2.5 px-4 text-xs font-bold text-white bg-blue-600 hover:bg-blue-700 rounded-xl shadow-sm active:scale-98 transition-all"
                  >
                    <Globe className="w-4 h-4" />
                    <span>Visit Website</span>
                  </a>
                </>
              ) : project.type === 'apk' ? (
                <button
                  onClick={() => onDownloadApk(project)}
                  className="flex items-center gap-2 py-3 px-5 text-xs font-bold text-white bg-slate-900 hover:bg-blue-600 rounded-2xl shadow-sm hover:shadow-md active:scale-98 transition-all duration-300 cursor-pointer"
                >
                  <Download className="w-4 h-4" />
                  <span>Download {project.title} (.apk)</span>
                </button>
              ) : (
                <a
                  href={project.link}
                  target="_blank"
                  rel="noreferrer"
                  className="flex items-center gap-2 py-3 px-5 text-xs font-bold text-white bg-blue-600 hover:bg-blue-700 rounded-2xl shadow-sm hover:shadow-md active:scale-98 transition-all duration-300"
                >
                  <Globe className="w-4 h-4" />
                  <span>Open Live Website</span>
                </a>
              )}
            </div>
          </div>
        </div>
      </div>

      {isViewerFullscreen && createPortal(
        <div
          className="fixed inset-0 z-[100] flex items-center justify-center bg-slate-50 p-2 sm:p-6 select-none cursor-zoom-out touch-manipulation"
          onDoubleClick={toggleViewerFullscreen}
          onTouchEnd={handleViewerTouchEnd}
          title="Double-click or double-tap to exit fullscreen"
        >
          <img
            src={project.galleryImages[currentSlide]}
            alt={`${project.title} Screenshot ${currentSlide + 1} fullscreen`}
            referrerPolicy="no-referrer"
            decoding="async"
            draggable={false}
            className="project-image max-w-full max-h-full w-auto h-auto object-contain"
          />

          <button
            onClick={(e) => {
              e.stopPropagation();
              toggleViewerFullscreen();
            }}
            onDoubleClick={(e) => e.stopPropagation()}
            className="absolute top-4 right-4 p-3 rounded-full bg-white/95 hover:bg-white text-slate-800 border border-slate-200 shadow-lg transition-all cursor-pointer"
            aria-label="Exit fullscreen"
            title="Exit fullscreen"
          >
            <Minimize2 className="w-5 h-5" />
          </button>

          {project.galleryImages.length > 1 && (
            <>
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  prevSlide();
                }}
                onDoubleClick={(e) => e.stopPropagation()}
                className="absolute left-3 sm:left-6 top-1/2 -translate-y-1/2 p-3 rounded-full bg-white/95 hover:bg-white text-slate-800 border border-slate-200 shadow-lg transition-all cursor-pointer"
                aria-label="Previous slide"
              >
                <ChevronLeft className="w-6 h-6" />
              </button>
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  nextSlide();
                }}
                onDoubleClick={(e) => e.stopPropagation()}
                className="absolute right-3 sm:right-6 top-1/2 -translate-y-1/2 p-3 rounded-full bg-white/95 hover:bg-white text-slate-800 border border-slate-200 shadow-lg transition-all cursor-pointer"
                aria-label="Next slide"
              >
                <ChevronRight className="w-6 h-6" />
              </button>
              <div className="absolute bottom-4 left-1/2 -translate-x-1/2 flex items-center gap-1.5 bg-white/95 border border-slate-200 px-3 py-1.5 rounded-full shadow-lg">
                {project.galleryImages.map((_, idx) => (
                  <button
                    key={idx}
                    onClick={(e) => {
                      e.stopPropagation();
                      setCurrentSlide(idx);
                    }}
                    className={`h-2 rounded-full transition-all ${
                      idx === currentSlide ? 'bg-blue-600 w-4' : 'bg-slate-300 w-2'
                    }`}
                    aria-label={`View image ${idx + 1}`}
                  />
                ))}
              </div>
            </>
          )}
        </div>,
        document.body,
      )}
    </>
  );
};
