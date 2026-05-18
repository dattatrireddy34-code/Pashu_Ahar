const mongoose = require('mongoose');

const CowSchema = new mongoose.Schema({
  name: {
    type: String,
    required: [true, 'Please add a name'],
  },
  breed: {
    type: String,
    required: [true, 'Please add a breed'],
  },
  age: {
    type: Number,
    required: [true, 'Please add age in months'],
  },
  weight: {
    type: Number,
    required: [true, 'Please add weight in kg'],
  },
  currentYield: {
    type: Number,
    default: 0,
  },
  targetYield: {
    type: Number,
    default: 0,
  },
  profileImage: {
    type: String,
  },
  owner: {
    type: mongoose.Schema.ObjectId,
    ref: 'User',
    required: true,
  },
  status: {
    type: [String],
    default: ['Milk Cow', 'Healthy'],
  },
  createdAt: {
    type: Date,
    default: Date.now,
  },
});

module.exports = mongoose.model('Cow', CowSchema);
