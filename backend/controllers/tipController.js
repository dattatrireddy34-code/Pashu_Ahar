const VeterinaryTip = require('../models/VeterinaryTip');

// @desc    Get all veterinary tips
// @route   GET /api/tips
// @access  Public
exports.getTips = async (req, res, next) => {
  try {
    const tips = await VeterinaryTip.find().sort('order');
    res.status(200).json({ success: true, count: tips.length, data: tips });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};

// @desc    Seed tips
// @route   POST /api/tips/seed
// @access  Private (Admin)
exports.seedTips = async (req, res, next) => {
  try {
    const initialTips = [
      { title: 'Fresh Water Always', description: 'Ensure your cows have access to clean, fresh water at all times. A dairy cow can drink up to 100 liters per day.', order: 1, category: 'Health' },
      { title: 'Balanced Diet', description: 'Feed a balanced mix of roughage and concentrates. Maintain correct ratios of fiber, protein, and energy for optimal milk production.', order: 2, category: 'Nutrition' },
      { title: 'Regular Vaccination', description: 'Vaccinate your herd regularly against FMD, HS, and BQ. Consult your local vet for a vaccination schedule.', order: 3, category: 'Health' },
      { title: 'Deworming Schedule', description: 'Every 3-4 months. Internal parasites reduce feed efficiency and can severely lower milk production.', order: 4, category: 'Health' },
    ];
    await VeterinaryTip.deleteMany();
    const tips = await VeterinaryTip.insertMany(initialTips);
    res.status(201).json({ success: true, data: tips });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};
