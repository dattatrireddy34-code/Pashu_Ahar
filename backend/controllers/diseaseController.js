const DiseaseCase = require('../models/DiseaseCase');
const Cow = require('../models/Cow');

exports.getDiseaseSummary = async (req, res) => {
  try {
    const cases = await DiseaseCase.find({ owner: req.user.id }).populate('cow');

    const stats = {
      totalCases: cases.length,
      activeCases: cases.filter(c => c.status === 'Active' || c.status === 'Under Treatment').length,
      recovered: cases.filter(c => c.status === 'Recovered').length,
      treatments: 18, // Dummy placeholder for now
    };

    const diseaseDistribution = {};
    cases.forEach(c => {
      diseaseDistribution[c.diseaseName] = (diseaseDistribution[c.diseaseName] || 0) + 1;
    });

    res.status(200).json({
      success: true,
      data: {
        stats,
        diseaseDistribution,
        cases
      }
    });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};

exports.addDiseaseCase = async (req, res) => {
  try {
    req.body.owner = req.user.id;
    const diseaseCase = await DiseaseCase.create(req.body);
    res.status(201).json({ success: true, data: diseaseCase });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};
