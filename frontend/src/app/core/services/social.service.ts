import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiBaseService } from './api-base.service';
import { FitnessGroup, SocialPost } from '../models';

@Injectable({ providedIn: 'root' })
export class SocialService extends ApiBaseService {
  getFeed(): Observable<SocialPost[]> {
    return this.get<SocialPost[]>('/social/posts/feed');
  }

  getGroups(): Observable<FitnessGroup[]> {
    return this.get<FitnessGroup[]>('/social/groups');
  }

  createGroup(data: {
    name: string;
    description?: string;
    weeklyWorkoutPlan?: string;
  }): Observable<FitnessGroup> {
    return this.post<FitnessGroup>('/social/groups', data);
  }

  addMember(groupId: string, userId: number, displayName?: string): Observable<FitnessGroup> {
    return this.post<FitnessGroup>(`/social/groups/${groupId}/members`, { userId, displayName });
  }

  createPost(groupId: string, content: string): Observable<SocialPost> {
    return this.post<SocialPost>(`/social/posts/groups/${groupId}`, { content });
  }

  upvote(id: string): Observable<SocialPost> {
    return this.post<SocialPost>(`/social/posts/${id}/upvote`, {});
  }

  downvote(id: string): Observable<SocialPost> {
    return this.post<SocialPost>(`/social/posts/${id}/downvote`, {});
  }
}
