const mongoose = require('mongoose');

const IngredientSchema = new mongoose.Schema({
  name: {
    type: String,
    required: [true, 'Please add an ingredient name'],
  },
  nutritionCP: {
    type: Number,
    required: [true, 'Please add nutrition CP percentage'],
  },
  pricePerKg: {
    type: Number,
    required: [true, 'Please add price per kg'],
  },
  type: {
    type: String,
    enum: ['Fodder', 'Concentrate', 'Supplement'],
    default: 'Concentrate',
  },
  owner: {
    type: mongoose.Schema.ObjectId,
    ref: 'User',
    required: true,
  },
  createdAt: {
    type: Date,
    default: Date.now,
  },
});

module.exports = mongoose.model('Ingredient', IngredientSchema);
