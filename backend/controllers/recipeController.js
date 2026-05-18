const Ingredient = require('../models/Ingredient');
const Cow = require('../models/Cow');

// @desc    Calculate feed recipe for a cow
// @route   GET /api/recipes/:cowId?yield=20
// @access  Private
exports.getRecipe = async (req, res, next) => {
  try {
    const cow = await Cow.findById(req.params.cowId);
    if (!cow) {
      return res.status(404).json({ success: false, message: 'Cow not found' });
    }

    const targetYield = req.query.yield || cow.targetYield || 20;

    // Fetch ingredients to get current prices
    const ingredients = await Ingredient.find({ owner: req.user.id });

    // Basic logic for demonstration (representing the UI screenshot)
    // In a real app, this would use a nutrition balancing algorithm
    const ratio = targetYield / 20; // Scale based on 20L example

    const recipe = [
      { name: 'Green Fodder (Maize)', recommended: (31.92 * ratio).toFixed(2), type: 'Fodder' },
      { name: 'Rice Straw', recommended: (3.8 * ratio).toFixed(2), type: 'Fodder' },
      { name: 'Maize', recommended: (4.92 * ratio).toFixed(2), type: 'Concentrate' },
      { name: 'Wheat Bran', recommended: (3.24 * ratio).toFixed(2), type: 'Concentrate' },
      { name: 'Mineral Mixture', recommended: (0.4 * ratio).toFixed(2), type: 'Supplement' },
    ];

    // Calculate total cost and total weight
    let totalWeight = 0;
    let totalCost = 0;

    recipe.forEach(item => {
      totalWeight += parseFloat(item.recommended);
      const ingredient = ingredients.find(ing => ing.name.includes(item.name)) || { pricePerKg: 0 };
      item.pricePerKg = ingredient.pricePerKg;
      item.cost = (item.recommended * item.pricePerKg).toFixed(2);
      totalCost += parseFloat(item.cost);
    });

    res.status(200).json({
      success: true,
      data: {
        cowName: cow.name,
        targetYield,
        totalWeight: totalWeight.toFixed(2),
        totalCost: totalCost.toFixed(2),
        recipe
      }
    });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};
