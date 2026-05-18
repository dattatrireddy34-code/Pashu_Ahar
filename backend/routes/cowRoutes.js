const express = require('express');
const {
  getCows,
  getStats,
  createCow,
  updateCow,
  deleteCow,
} = require('../controllers/cowController');
const { protect } = require('../middleware/authMiddleware');
const { upload } = require('../utils/cloudinary');

const router = express.Router();

router.use(protect); // Protect all routes below

router.get('/', getCows);
router.get('/stats', getStats);
router.post('/', upload.single('profileImage'), createCow);
router.put('/:id', upload.single('profileImage'), updateCow);
router.delete('/:id', deleteCow);

module.exports = router;
