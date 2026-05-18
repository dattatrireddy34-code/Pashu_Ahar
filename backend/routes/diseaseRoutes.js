const express = require('express');
const { getDiseaseSummary, addDiseaseCase } = require('../controllers/diseaseController');
const { protect } = require('../middleware/authMiddleware');

const router = express.Router();

router.use(protect);

router.get('/summary', getDiseaseSummary);
router.post('/case', addDiseaseCase);

module.exports = router;
