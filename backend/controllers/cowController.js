const Cow = require('../models/Cow');

// @desc    Get all cows
// @route   GET /api/cows
// @access  Private
exports.getCows = async (req, res, next) => {
  try {
    const cows = await Cow.find({ owner: req.user.id });
    res.status(200).json({ success: true, count: cows.length, data: cows });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};

// @desc    Get dashboard stats
// @route   GET /api/cows/stats
// @access  Private
exports.getStats = async (req, res, next) => {
  try {
    const cows = await Cow.find({ owner: req.user.id });

    const totalCows = cows.length;
    const todayYield = cows.reduce((acc, cow) => acc + cow.currentYield, 0);
    // Dummy efficiency and due heat for now
    const avgEfficiency = totalCows > 0 ? 82 : 0;
    const dueHeat = totalCows > 0 ? 3 : 0;

    res.status(200).json({
      success: true,
      data: {
        totalCows,
        todayYield,
        avgEfficiency,
        dueHeat
      }
    });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};

// @desc    Create new cow
// @route   POST /api/cows
// @access  Private
exports.createCow = async (req, res, next) => {
  try {
    req.body.owner = req.user.id;

    // If image uploaded via cloudinary in registration, we might do same for cow
    if (req.file) {
      req.body.profileImage = req.file.path;
    }

    const cow = await Cow.create(req.body);
    res.status(201).json({ success: true, data: cow });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};

// @desc    Update cow
// @route   PUT /api/cows/:id
// @access  Private
exports.updateCow = async (req, res, next) => {
  try {
    let cow = await Cow.findById(req.params.id);

    if (!cow) {
      return res.status(404).json({ success: false, message: 'Cow not found' });
    }

    // Make sure user is cow owner
    if (cow.owner.toString() !== req.user.id) {
      return res.status(401).json({ success: false, message: 'Not authorized' });
    }

    if (req.file) {
      req.body.profileImage = req.file.path;
    }

    cow = await Cow.findByIdAndUpdate(req.params.id, req.body, {
      new: true,
      runValidators: true,
    });

    res.status(200).json({ success: true, data: cow });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};

// @desc    Delete cow
// @route   DELETE /api/cows/:id
// @access  Private
exports.deleteCow = async (req, res, next) => {
  try {
    const cow = await Cow.findById(req.params.id);

    if (!cow) {
      return res.status(404).json({ success: false, message: 'Cow not found' });
    }

    if (cow.owner.toString() !== req.user.id) {
      return res.status(401).json({ success: false, message: 'Not authorized' });
    }

    await cow.deleteOne();

    res.status(200).json({ success: true, data: {} });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};
