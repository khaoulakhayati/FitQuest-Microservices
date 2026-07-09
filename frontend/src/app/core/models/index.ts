export interface User {
  id: number;
  email: string;
  username: string;
  roles?: string[];
  profile?: {
    displayName?: string;
  };
  displayName?: string;
  avatarUrl?: string;
  level?: number;
  xp?: number;
}

export interface AuthResponse {
  token?: string;
  accessToken?: string;
  refreshToken?: string;
  keycloakAccessToken?: string;
  keycloakRefreshToken?: string;
  identityProvider?: string;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  username: string;
  displayName?: string;
  accountType?: 'user' | 'coach';
}

export interface Workout {
  id: number;
  userId?: number | null;
  sharedTemplate?: boolean;
  name: string;
  type?: string;
  muscleGroup?: string;
  difficulty?: string;
  description?: string;
  durationMinutes?: number;
  estimatedDurationMinutes?: number;
  caloriesBurned?: number;
  completedAt?: string;
  exercises?: Exercise[];
}

export interface Exercise {
  id: number;
  name: string;
  description?: string;
  muscleGroup: string;
  difficulty?: string;
  caloriesPerMinute?: number;
}

export interface WorkoutLogRequest {
  workoutId: number;
  durationMinutes: number;
  sets?: number;
  reps?: number;
  weightKg?: number;
  notes?: string;
}

export interface CreateWorkoutRequest {
  name: string;
  description?: string;
  difficulty: string;
  estimatedDurationMinutes: number;
  exerciseIds: number[];
}

export interface Meal {
  id: number;
  name: string;
  mealType?: string;
  calories?: number;
  protein?: number;
  carbs?: number;
  fat?: number;
  totalCalories?: number;
  totalProteinG?: number;
  totalCarbsG?: number;
  totalFatG?: number;
  loggedAt?: string;
  consumedAt?: string;
  items?: MealItem[];
}

export interface MealItem {
  foodItemId: number;
  foodName: string;
  quantityGrams: number;
  calories: number;
  proteinG: number;
  carbsG: number;
  fatG: number;
}

export interface FoodItem {
  id: number;
  name: string;
  brand?: string;
  category?: string;
  caloriesPer100g: number;
  proteinPer100g: number;
  carbsPer100g: number;
  fatPer100g: number;
  defaultServingGrams: number;
}

export interface CreateMealRequest {
  name: string;
  mealType: 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACK';
  consumedAt?: string;
  items: { foodItemId: number; quantityGrams: number }[];
}

export interface Challenge {
  id: string;
  title: string;
  description: string;
  type?: string;
  xpReward?: number;
  progress?: number;
  target?: number;
  goalPoints?: number;
  status: string;
  startDate?: string;
  endDate?: string;
}

export interface LeaderboardEntry {
  rank: number;
  userId: number;
  username?: string;
  xp?: number;
  totalXp?: number;
  level: number;
  achievementCount?: number;
}

export interface LeaderboardResponse {
  entries: LeaderboardEntry[];
  limit: number;
}

export interface SocialPost {
  id: string;
  groupId?: string;
  authorId?: number;
  authorUsername: string;
  content: string;
  likes?: number;
  upvotes: number;
  downvotes: number;
  myVote?: 'UP' | 'DOWN' | null;
  createdAt: string;
}

export interface FitnessGroup {
  id: string;
  name: string;
  description?: string;
  coachId: number;
  weeklyWorkoutPlan?: string;
  memberCount: number;
  createdAt: string;
}

export interface Notification {
  id: number;
  title: string;
  message: string;
  read: boolean;
  createdAt: string;
}

export interface AiCoachMessage {
  id?: number;
  role: 'user' | 'assistant';
  content: string;
  timestamp?: string;
}

export interface DashboardStats {
  totalWorkouts: number;
  weeklyCalories: number;
  nutritionCalories: number;
  activeChallenges: number;
  currentStreak: number;
  xpThisWeek: number[];
  workoutsByDay: { day: string; count: number }[];
}
