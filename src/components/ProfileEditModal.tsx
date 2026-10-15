import React, { useState } from 'react';
import { X, Save, Download, Copy, Check, SlidersHorizontal, RefreshCw } from 'lucide-react';
import { PortfolioProfile, Project, initialProfile } from '../data/portfolioData';

interface ProfileEditModalProps {
  profile: PortfolioProfile;
  projects: Project[];
  isOpen: boolean;
  onClose: () => void;
  onSaveProfile: (updated: PortfolioProfile) => void;
  onResetToDefault: () => void;
}

export const ProfileEditModal: React.FC<ProfileEditModalProps> = ({
  profile,
  projects,
  isOpen,
  onClose,
  onSaveProfile,
  onResetToDefault,
}) => {
  const [activeTab, setActiveTab] = useState<'profile' | 'metrics' | 'export'>('profile');
  const [formData, setFormData] = useState<PortfolioProfile>(profile);
  const [copied, setCopied] = useState(false);
  const [savedToast, setSavedToast] = useState(false);

  if (!isOpen) return null;

  const handleSave = () => {
    onSaveProfile(formData);
    setSavedToast(true);
    setTimeout(() => {
      setSavedToast(false);
      onClose();
    }, 700);
  };

  const handleExportJSON = () => {
    const dataToExport = {
      profile: formData,
      projects,
      exportedAt: new Date().toISOString(),
    };
    const blob = new Blob([JSON.stringify(dataToExport, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `portfolio_${formData.name.toLowerCase().replace(/\s+/g, '_')}.json`;
    a.click();
    URL.revokeObjectURL(url);
  };

  const handleCopyJSON = () => {
    const dataToExport = {
      profile: formData,
      projects,
      exportedAt: new Date().toISOString(),
    };
    navigator.clipboard.writeText(JSON.stringify(dataToExport, null, 2));
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6 bg-slate-950/85 backdrop-blur-md overflow-y-auto">
      <div 
        className="relative w-full max-w-3xl my-auto bg-slate-900 border border-slate-700/80 rounded-2xl shadow-2xl overflow-hidden text-slate-100"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-800 bg-slate-950/80">
          <div className="flex items-center gap-2">
            <SlidersHorizontal className="w-4 h-4 text-blue-400" />
            <h3 className="text-base font-bold text-white">Portfolio Customizer & Data Backup</h3>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 text-slate-400 hover:text-white hover:bg-slate-800 rounded-lg transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Tab Navigation */}
        <div className="flex border-b border-slate-800 bg-slate-950/50 px-6 pt-2">
          {(['profile', 'metrics', 'export'] as const).map((tab) => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              className={`px-4 py-2.5 text-xs font-semibold capitalize border-b-2 transition-all ${
                activeTab === tab
                  ? 'border-blue-500 text-white'
                  : 'border-transparent text-slate-400 hover:text-slate-200'
              }`}
            >
              {tab === 'profile' ? 'Profile Details' : tab === 'metrics' ? 'Metrics & KPIs' : 'Backup & Export'}
            </button>
          ))}
        </div>

        {/* Form Body */}
        <div className="p-6 max-h-[60vh] overflow-y-auto space-y-4">
          {activeTab === 'profile' && (
            <div className="space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className="space-y-1">
                  <label className="text-xs font-medium text-slate-300">Full Name</label>
                  <input
                    type="text"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-sm text-white focus:outline-none focus:border-blue-500"
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-medium text-slate-300">Professional Title</label>
                  <input
                    type="text"
                    value={formData.role}
                    onChange={(e) => setFormData({ ...formData, role: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-sm text-white focus:outline-none focus:border-blue-500"
                  />
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className="space-y-1">
                  <label className="text-xs font-medium text-slate-300">Email Address</label>
                  <input
                    type="email"
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-sm text-white focus:outline-none focus:border-blue-500"
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-medium text-slate-300">Location</label>
                  <input
                    type="text"
                    value={formData.location}
                    onChange={(e) => setFormData({ ...formData, location: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-sm text-white focus:outline-none focus:border-blue-500"
                  />
                </div>
              </div>

              <div className="space-y-1">
                <label className="text-xs font-medium text-slate-300">Availability Status</label>
                <input
                  type="text"
                  value={formData.availability}
                  onChange={(e) => setFormData({ ...formData, availability: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-sm text-white focus:outline-none focus:border-blue-500"
                />
              </div>

              <div className="space-y-1">
                <label className="text-xs font-medium text-slate-300">Hero Headline</label>
                <textarea
                  rows={2}
                  value={formData.bioHeadline}
                  onChange={(e) => setFormData({ ...formData, bioHeadline: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-sm text-white focus:outline-none focus:border-blue-500 resize-none"
                />
              </div>

              <div className="space-y-1">
                <label className="text-xs font-medium text-slate-300">Primary Bio</label>
                <textarea
                  rows={3}
                  value={formData.bioParagraph1}
                  onChange={(e) => setFormData({ ...formData, bioParagraph1: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-950 border border-slate-800 rounded-lg text-sm text-white focus:outline-none focus:border-blue-500 resize-none"
                />
              </div>
            </div>
          )}

          {activeTab === 'metrics' && (
            <div className="space-y-4">
              <p className="text-xs text-slate-400">
                Update the 4 prominent proof metrics that appear on your hero section.
              </p>
              {formData.metrics.map((metric, idx) => (
                <div key={idx} className="p-3 bg-slate-950 rounded-xl border border-slate-800 space-y-2">
                  <div className="grid grid-cols-2 gap-3">
                    <div>
                      <label className="text-[11px] text-slate-400">Metric Value</label>
                      <input
                        type="text"
                        value={metric.value}
                        onChange={(e) => {
                          const updated = [...formData.metrics];
                          updated[idx].value = e.target.value;
                          setFormData({ ...formData, metrics: updated });
                        }}
                        className="w-full px-2.5 py-1.5 bg-slate-900 border border-slate-800 rounded text-sm text-white"
                      />
                    </div>
                    <div>
                      <label className="text-[11px] text-slate-400">Label</label>
                      <input
                        type="text"
                        value={metric.label}
                        onChange={(e) => {
                          const updated = [...formData.metrics];
                          updated[idx].label = e.target.value;
                          setFormData({ ...formData, metrics: updated });
                        }}
                        className="w-full px-2.5 py-1.5 bg-slate-900 border border-slate-800 rounded text-sm text-white"
                      />
                    </div>
                  </div>
                  <div>
                    <label className="text-[11px] text-slate-400">Contextual Detail</label>
                    <input
                      type="text"
                      value={metric.detail}
                      onChange={(e) => {
                        const updated = [...formData.metrics];
                        updated[idx].detail = e.target.value;
                        setFormData({ ...formData, metrics: updated });
                      }}
                      className="w-full px-2.5 py-1.5 bg-slate-900 border border-slate-800 rounded text-xs text-slate-300"
                    />
                  </div>
                </div>
              ))}
            </div>
          )}

          {activeTab === 'export' && (
            <div className="space-y-5">
              <div className="p-4 rounded-xl bg-blue-950/30 border border-blue-800/40 text-xs text-blue-200 leading-relaxed">
                <span className="font-semibold block mb-1">Backup Protection:</span>
                Download your complete portfolio data as a JSON file or copy it to your clipboard. You can keep this file safe on your computer anytime.
              </div>

              <div className="flex flex-wrap gap-3">
                <button
                  onClick={handleExportJSON}
                  className="flex items-center gap-2 px-4 py-2.5 text-xs font-semibold text-white bg-blue-600 hover:bg-blue-500 rounded-lg transition-colors"
                >
                  <Download className="w-4 h-4" />
                  <span>Download Backup JSON</span>
                </button>

                <button
                  onClick={handleCopyJSON}
                  className="flex items-center gap-2 px-4 py-2.5 text-xs font-semibold text-slate-200 bg-slate-800 hover:bg-slate-700 rounded-lg transition-colors"
                >
                  {copied ? <Check className="w-4 h-4 text-emerald-400" /> : <Copy className="w-4 h-4" />}
                  <span>{copied ? 'Copied to Clipboard' : 'Copy Portfolio JSON'}</span>
                </button>

                <button
                  onClick={() => {
                    setFormData(initialProfile);
                    onResetToDefault();
                  }}
                  className="flex items-center gap-1.5 px-3 py-2 text-xs font-medium text-slate-400 hover:text-rose-400 transition-colors ml-auto"
                >
                  <RefreshCw className="w-3.5 h-3.5" />
                  <span>Restore Factory Defaults</span>
                </button>
              </div>
            </div>
          )}
        </div>

        {/* Footer Actions */}
        <div className="flex items-center justify-between px-6 py-4 border-t border-slate-800 bg-slate-950/80">
          <span className="text-xs text-emerald-400 font-medium">
            {savedToast ? 'Changes Saved!' : ''}
          </span>
          <div className="flex items-center gap-3">
            <button
              onClick={onClose}
              className="px-4 py-2 text-xs font-medium text-slate-400 hover:text-white transition-colors"
            >
              Cancel
            </button>
            <button
              onClick={handleSave}
              className="flex items-center gap-1.5 px-5 py-2 text-xs font-semibold text-white bg-blue-600 hover:bg-blue-500 rounded-lg shadow-sm transition-all"
            >
              <Save className="w-3.5 h-3.5" />
              <span>Apply Changes</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
