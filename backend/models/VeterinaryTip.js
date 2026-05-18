const mongoose = require('mongoose');

const VeterinaryTipSchema = new mongoose.Schema({
  title: {
    type: String,
    required: [true, 'Please add a title'],
  },
  description: {
    type: String,
    required: [true, 'Please add a description'],
  },
  category: {
    type: String,
    enum: ['Nutrition', 'Health', 'Management', 'Housing'],
  },
  icon: String, // String identifier for app icons
  order: Number,
  createdAt: {
    type: Date,
    default: Date.now,
  },
});

module.exports = mongoose.model('VeterinaryTip', VeterinaryTipSchema);
