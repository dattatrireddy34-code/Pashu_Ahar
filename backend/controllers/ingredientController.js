const Ingredient = require('../models/Ingredient');

// @desc    Get all ingredients for user
// @route   GET /api/ingredients
// @access  Private
exports.getIngredients = async (req, res, next) => {
  try {
    const ingredients = await Ingredient.find({ owner: req.user.id });
    res.status(200).json({ success: true, count: ingredients.length, data: ingredients });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};

// @desc    Add or Update ingredient
// @route   POST /api/ingredients
// @access  Private
exports.upsertIngredient = async (req, res, next) => {
  try {
    req.body.owner = req.user.id;

    // Check if ingredient with same name exists for this user
    let ingredient = await Ingredient.findOne({
      owner: req.user.id,
      name: req.body.name
    });

    if (ingredient) {
      ingredient = await Ingredient.findByIdAndUpdate(ingredient._id, req.body, {
        new: true,
        runValidators: true
      });
    } else {
      ingredient = await Ingredient.create(req.body);
    }

    res.status(200).json({ success: true, data: ingredient });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};

// @desc    Seed initial ingredients
// @route   POST /api/ingredients/seed
// @access  Private
exports.seedIngredients = async (req, res, next) => {
  try {
    const initialIngredients = [
      { name: 'Green Fodder (Maize)', nutritionCP: 8, pricePerKg: 4.5, type: 'Fodder', owner: req.user.id },
      { name: 'Rice Straw', nutritionCP: 3, pricePerKg: 3.0, type: 'Fodder', owner: req.user.id },
      { name: 'Maize', nutritionCP: 9, pricePerKg: 22.0, type: 'Concentrate', owner: req.user.id },
      { name: 'Wheat Bran', nutritionCP: 15, pricePerKg: 22.0, type: 'Concentrate', owner: req.user.id },
      { name: 'Soybean Meal', nutritionCP: 44, pricePerKg: 32.0, type: 'Concentrate', owner: req.user.id },
      { name: 'Mineral Mixture', nutritionCP: 0, pricePerKg: 45.0, type: 'Supplement', owner: req.user.id },
    ];

    await Ingredient.deleteMany({ owner: req.user.id });
    const ingredients = await Ingredient.insertMany(initialIngredients);

    res.status(201).json({ success: true, data: ingredients });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};
