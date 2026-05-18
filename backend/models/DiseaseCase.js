const mongoose = require('mongoose');

const DiseaseCaseSchema = new mongoose.Schema({
  owner: {
    type: mongoose.Schema.ObjectId,
    ref: 'User',
    required: true,
  },
  cow: {
    type: mongoose.Schema.ObjectId,
    ref: 'Cow',
    required: true,
  },
  diseaseName: {
    type: String,
    required: true,
  },
  status: {
    type: String,
    enum: ['Active', 'Recovered', 'Under Treatment'],
    default: 'Active',
  },
  severity: {
    type: String,
    enum: ['Mild', 'Moderate', 'Severe'],
    default: 'Moderate',
  },
  symptoms: String,
  detectedOn: {
    type: Date,
    default: Date.now,
  },
  lastTreatment: Date,
});

module.exports = mongoose.model('DiseaseCase', DiseaseCaseSchema);
